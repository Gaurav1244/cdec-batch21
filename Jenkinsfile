pipeline {
    agent any

    stages {

        stage('PULL') {
            steps {
                // Clone your repo from main branch
                git branch: 'main', url: 'https://github.com/Gaurav1244/cdec-batch21.git'
            }
        }

        stage('BUILD') {
            steps {
                // Build Maven project in backend folder
                dir('backend') {
                    sh 'mvn clean package -DskipTests'
                }
            }
        }

        stage('SONARQUBE ANALYSIS') {
            steps {
                // Run SonarQube analysis with configured server
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
                // Wait for SonarQube quality gate result
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

       stage('DELIVERY') {
        steps {
          sh 'aws s3 cp backend/target/student-registration-backend-0.0.1-SNAPSHOT.jar s3://my-simple-tfstate-bucket-12345/student-artifact.jar'
              }
          }

        stage('DEPLOY') {
            steps {
                echo "DEPLOY SUCCESS"
            }
        }

    }
}

