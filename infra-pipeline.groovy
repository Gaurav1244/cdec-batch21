pipeline {
  agent any

  stages {
    stage('PULL') {
      steps {
        git 'https://github.com/Gaurav1244/cdec-b21.git'
      }
    }

    stage('TF INIT') {
      steps {
        dir('terraform') {
          sh 'terraform init'
        }
      }
    }

    stage('TF PLAN') {
      steps {
        dir('terraform') {
          sh 'terraform plan'
        }
      }
    }

    stage('TF APPLY') {
      steps {
        dir('terraform') {
          sh 'terraform apply -auto-approve'
        }
      }
    }
  }
}
