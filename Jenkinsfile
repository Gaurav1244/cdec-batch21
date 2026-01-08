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
                            credentialsId: 'aws-creds',
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

        stage('DEPLOY') {
            steps {
                echo "DEPLOY SUCCESS"
            }
        }
    }
}
