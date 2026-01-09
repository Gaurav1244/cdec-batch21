pipeline {
    agent any

    environment {
        AWS_REGION = "ap-south-1"
        ECR_REPO = "730335465515.dkr.ecr.ap-south-1.amazonaws.com/student-app"
        EKS_CLUSTER = "student-cluster"
    }

    stages {

        stage('PULL') {
            steps {
                git branch: 'main', url: 'https://github.com/Gaurav1244/cdec-batch21.git'
            }
        }

        stage('BUILD') {
            steps {
                dir('backend') {
                    sh 'mvn clean package -DskipTests'
                }
            }
        }

        stage('SONARQUBE ANALYSIS') {
            steps {
                withSonarQubeEnv('mysonarqube') {
                    dir('backend') {
                        sh '''
                        mvn org.sonarsource.scanner.maven:sonar-maven-plugin:sonar \
                        -Dsonar.projectKey=myapp \
                        -Dsonar.projectName=myapp
                        '''
                    }
                }
            }
        }

        stage('QUALITY GATE') {
            steps {
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('DELIVERY TO S3') {
            steps {
                dir('backend') {
                    sh '''
                    aws s3 cp target/student-registration-backend-0.0.1-SNAPSHOT.jar \
                    s3://my-simple-tfstate-bucket-12345/student-artifact.jar
                    '''
                }
            }
        }

        stage('DOCKER BUILD & PUSH') {
            steps {
                dir('backend') {
                    sh '''
                    aws ecr get-login-password --region $AWS_REGION \
                    | docker login --username AWS --password-stdin $ECR_REPO

                    docker build -t student-app .
                    docker tag student-app:latest $ECR_REPO:latest
                    docker push $ECR_REPO:latest
                    '''
                }
            }
        }

        stage('DEPLOY TO EKS') {
            steps {
                script {
                    def clusterExists = sh(
                        script: "aws eks describe-cluster --name $EKS_CLUSTER --region $AWS_REGION >/dev/null 2>&1 && echo 'yes' || echo 'no'",
                        returnStdout: true
                    ).trim()

                    if (clusterExists == 'yes') {
                        sh """
                        echo 'Cluster exists. Updating kubeconfig...'
                        aws eks update-kubeconfig --region $AWS_REGION --name $EKS_CLUSTER

                        sed -i "s|<ECR-IMAGE-URL>|$ECR_REPO:latest|g" backend/k8s/deployment.yml

                        kubectl apply -f backend/k8s/deployment.yml
                        kubectl apply -f backend/k8s/service.yml

                        kubectl get pods
                        kubectl get svc
                        """
                    } else {
                        error("EKS cluster '$EKS_CLUSTER' not found in region '$AWS_REGION'. Deployment aborted.")
                    }
                }
            }
        }
    }
}

