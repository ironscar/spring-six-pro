pipeline {
    environment {
        registry = 'ironscar/spring-six-pro'
        registryCredential = 'docker-hub'
        githubPeronalToken = credentials('github-personal-token')
        ansibleStageVaultPass = credentials('ansible-sbd-stage-vault-pass')
        ansibleProdVaultPass = credentials('ansible-sbd-prod-vault-pass')
        dockerImage = ''
        pomVersion = ''
        ansibleMainDir = 'vagrant-debian-bullseye/ansible-learning/envs/prod'
        ansibleSnapshotDir = 'vagrant-debian-bullseye/ansible-learning/envs/stage'
        ansiblePlaybookDir = 'vagrant-debian-bullseye/ansible-learning/playbooks'
    }
    agent any
    // need to use the names used in global config in jenkins
    tools {
        maven 'Maven-3.8.4'
        jdk 'JDK'
        dockerTool 'Docker-19.03.13'
    }
    stages {
        // dont really need this first step other than to check
        stage ('init-check') {
            steps {
                sh '''
                    echo "M2_HOME = ${M2_HOME}"
                    echo "JAVA_HOME = ${JAVA_HOME}"
                    echo "$BUILD_NUMBER"
                    docker --version
                '''
            }
        }
        stage('build-test') {
            steps {
                /*
                 * need to checkout and pull here as otherwise it keeps complaining
                 * that pull is up to date but push is behind remote etc
                 */
                sh 'git checkout $BRANCH_NAME && git pull origin $BRANCH_NAME'

                // the script is to update version using Pipeline Utility Steps plugin
                script {
                    def pom = readMavenPom()
                    pomVersion = pom.getVersion()
                    echo "${pomVersion}"
                    def versionList = pomVersion.tokenize(".")
                    def majorVersion = versionList[0]
                    def middleVersion = versionList[1]
                    pomVersion = majorVersion + "." + middleVersion + "." + BUILD_NUMBER + "-SNAPSHOT"
                    pom.version = pomVersion
                    echo "${pomVersion}"
                    writeMavenPom model: pom
                }

                /*
                 * run install after version update as otherwise dockerfile version will be wrong
                 * target is going to have old version but it will try to read new version
                 * commit version update if install is successful
                 */
                sh 'mvn clean install'
                sh 'git commit -am "update: version update by jenkins"'
            }
        }
        stage("package") {
            steps {
                script {
                    // add the computed pom version here so that dockerfile picks correct files to copy
                    dockerImage = docker.build(registry + ':' + BUILD_NUMBER, '--build-arg VERSION=' + pomVersion + ' .')
                }
            }
        }
        stage("publish") {
            steps {
                // can comment this due to it taking very long time for first time
                script {
                    try {
                        docker.withRegistry('', registryCredential) {

                            // allows pushing same image with different tag if branch is snapshot
                            if (BRANCH_NAME == 'snapshot') {
                                dockerImage.push('snapshot')
                            } else {
                                dockerImage.push()
                            }
                        }

                        // push version update commit
                        sh 'git push https://${githubPeronalToken_PSW}@github.com/ironscar/spring-six-pro.git $BRANCH_NAME'
                    } catch (err) {
                        echo "Failed publish: ${err}"

                        // reset branch to remote branch
                        sh 'git reset --hard origin/$BRANCH_NAME'

                        // so that next step doesn't continue
                        throw err;
                    } finally {
                        sh 'docker rmi $registry:$BUILD_NUMBER'

                        if (BRANCH_NAME == 'snapshot') {
                            // we need this because it seems pushing it with different tag actually creates the image as well which we ought to cleanup
                            sh 'docker rmi $registry:snapshot'
                        }
                    }
                }
            }
        }
        stage("deploy") {
            steps {
                script {
                    try {
                        // git fetch the ansible repo and run the playbook, remove it later
                        sh 'git clone https://${githubPeronalToken_PSW}@github.com/ironscar/vagrant-debian-bullseye.git'

                        // test which inventory file to run based on the branch run
                        script {
                            switch(BRANCH_NAME) {
                                case 'snapshot': 
                                    writeFile(file: 'password-file', text: ansibleStageVaultPass_PSW)
                                    sh 'ansible-playbook -i ${ansibleSnapshotDir}/inventory.yml --vault-id stage_vault@password-file ${ansiblePlaybookDir}/docker_playbook.yml --tags "spring-six-pro,common"'
                                    break
                                case 'main':
                                    writeFile(file: 'password-file', text: ansibleProdVaultPass_PSW)
                                    sh 'ansible-playbook -i ${ansibleMainDir}/inventory.yml --vault-id prod_vault@password-file ${ansiblePlaybookDir}/docker_playbook.yml --tags "spring-six-pro,common"'
                                    break
                                default: 
                                    echo 'No matching branch'
                            }
                        }
                    } catch (err) {
                        echo "Failed deploy: ${err}"

                        // so that next step doesn't continue
                        throw err;
                    } finally {
                        sh 'rm -rf vagrant-debian-bullseye'
                        sh 'rm -f password-file'
                    }
                }
            }
        }
    }
}