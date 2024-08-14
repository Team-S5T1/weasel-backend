pipeline {
    agent any

    tools {
        gradle 'Gradle'
    }

    environment {
        AWS_REGION = 'us-east-1'
        ECR_REPOSITORY = '393035689023.dkr.ecr.us-east-1.amazonaws.com'
        IMAGE_REPO_NAME = 'weasel-backend'
        IMAGE_TAG = "weasel-backend-${env.BUILD_NUMBER}"
        REPOSITORY_URL = '393035689023.dkr.ecr.us-east-1.amazonaws.com/weasel-backend'
        AWS_ACCOUNT_ID = '393035689023'
        AWS_CREDENTIAL = 'weasel-AWS-Credential'
        GIT_URL = 'https://github.com/Team-S5T1/weasel-backend.git'
        GIT_NAME = 'KKamJi98'
        GIT_MAIL = 'xowl5460@naver.com'
        GIT_SSH_ADD = 'git@github.com:Team-S5T1/weasel-k8s-manifests.git'
        GIT_CREDENTIAL = 'github-credential'
        SLACK_CHANNEL = '#alarm-jenkins'
        SLACK_CREDENTIALS_ID = 'weasel-slack-alarm'
    }

    stages {
        stage('Send message to slack') {
            steps {
                script {
                    slackSend(channel: SLACK_CHANNEL,
                            message: "Jenkins pipeline started: ${env.JOB_NAME} #${env.BUILD_NUMBER} - ${env.BUILD_URL}",
                            attachments: [[
                                color: '#0000ff',
                                text: 'Jenkins start!'
                            ]])
                }
            }
        }
        stage('Login to ECR') {
            steps {
                script {
                    withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', credentialsId: "${AWS_CREDENTIAL}"]]) {
                        sh """
                            aws ecr get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin ${ECR_REPOSITORY}
                        """
                    }
                }
            }
        }

        stage('Cloning Git') {
            steps {
                checkout([$class: 'GitSCM', branches: [[name: '*/main']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: '', url: "${GIT_URL}"]]])
            }
        }

        stage('Build') {
            steps {
                script {
                    sh '''
                        chmod +x ./gradlew
                        ./gradlew clean build -x test --no-daemon
                    '''
                }
            }
        }

                stage('Building image') {
            steps{
                script {
                        // sh "docker build -t ${IMAGE_REPO_NAME}:${IMAGE_TAG} ."
                        sh "docker build -t ${REPOSITORY_URL}:${IMAGE_TAG} ."
                }
            }
        }

        stage('Pushing to ECR') {
            steps {
                script {
                    // sh """docker tag ${IMAGE_REPO_NAME}:${IMAGE_TAG} ${REPOSITORY_URI}:${IMAGE_TAG}"""
                    // sh """docker push ${REPOSITORY_URI}:${IMAGE_TAG}"""
                    sh "docker push ${REPOSITORY_URL}:${IMAGE_TAG}"
                }
            }
        }
        
        stage('Delete Docker images') {
            steps {
                script {
                    // sh """docker rmi ${IMAGE_REPO_NAME}:${IMAGE_TAG}"""
                    sh "docker rmi ${REPOSITORY_URL}:${IMAGE_TAG}"
                }
            }
        }

        stage('k8s manifest file update') {
            steps {
                sh "git clean -fdx"  // 클론 후 불필요한 파일 삭제
                git credentialsId: GIT_CREDENTIAL,
                url: GIT_SSH_ADD,
                branch: 'main'

                sh "git config --global user.email ${GIT_MAIL}"
                sh "git config --global user.name ${GIT_NAME}"
                sh "sed -i 's@${REPOSITORY_URL}:.*@${REPOSITORY_URL}:${IMAGE_TAG}@g' weasel/backend/weasel-backend-deployment.yaml"
                sh "git add ."
                sh "git commit -m 'fix:${REPOSITORY_URL} ${IMAGE_TAG} image versioning'"
                sh "git branch -M main"
                sh "git remote remove origin"
                sh "git remote add origin ${GIT_SSH_ADD}"
                sh "git push -u origin main"
            }   
            post {
                failure {
                    echo 'k8s manifest file update failure'
                }
                success {
                    echo 'k8s manifest file update success'  
                }
            }
        }
    }

    post {
        success {
            slackSend(channel: SLACK_CHANNEL,
                    message: "Build succeeded: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                    attachments: [[
                        color: '#36a64f',
                        text: 'Build succeeded successfully.'
                    ]])
        }
        failure {
            slackSend(channel: SLACK_CHANNEL,
                    message: "Build failed: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                    attachments: [[
                        color: '#ff0000',
                        text: 'Build failed. Please check the details.'
                    ]])
        }
        unstable {
            slackSend(channel: SLACK_CHANNEL,
                    message: "Build unstable: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                    attachments: [[
                        color: '#f39c12',
                        text: 'Build unstable. Please check the details.'
                    ]])
        }
    }
}
