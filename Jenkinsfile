pipeline {
    agent any

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

        stage('DELIVERY') {
            steps {
                dir('backend') {
                    withCredentials([
                        usernamePassword(
                            credentialsId: 'aws-cred',
                            usernameVariable: 'AWS_ACCESS_KEY_ID',
                            passwordVariable: 'AWS_SECRET_ACCESS_KEY'
                        )
                    ]) {
                        sh '''
                        export AWS_DEFAULT_REGION=ap-south-1
                        aws s3 cp target/student-registration-backend-0.0.1-SNAPSHOT.jar \
                        s3://my-simple-tfstate-bucket-12345/student-artifact.jar
                        '''
                    }
                }
            }
        }

        stage('DOCKER BUILD & PUSH') {
  steps {
    dir('backend') {
      sh '''
      aws ecr get-login-password --region eu-west-1 | docker login --username AWS --password-stdin 730335465515.dkr.ecr.ap-south-1.amazonaws.com/student-app
      docker build -t student-app 
      docker tag student-app:latest 730335465515.dkr.ecr.ap-south-1.amazonaws.com/student-app/student-app:latest
      docker push 730335465515.dkr.ecr.ap-south-1.amazonaws.com/student-app/student-app:latest
      '''
    }
  }
}

    stage('DEPLOY TO EKS') {
  steps {
    sh '''
    aws eks update-kubeconfig --region ap-south-1 --name student-cluster

    sed -i "s|<ECR-IMAGE-URL>|730335465515.dkr.ecr.ap-south-1.amazonaws.com/student-app/student-app:latest|g" backend/k8s/deployment.yml

    kubectl apply -f backend/k8s/deployment.yml
    kubectl get pods
    kubectl get svc
    '''
  }
}
