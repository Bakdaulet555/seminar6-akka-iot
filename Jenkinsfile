pipeline {
    agent any

    stages {
        stage('Checkout Source Code') {
            steps {
                checkout scm
            }
        }

        stage('Compile & Unit Tests') {
            steps {
                sh 'mvn clean test'
            }
        }

        stage('Generate Coverage Metrics') {
            steps {
                sh 'mvn verify -Pmetrics'
            }
        }
    }

    post {
        always {
            junit allowEmptyResults: true,
                  testResults: '**/target/surefire-reports/*.xml, **/target/scalatest-reports/*.xml'

            archiveArtifacts allowEmptyArchive: true,
                artifacts: '**/target/site/jacoco/**'
        }

        success {
            echo 'SUCCESS: IoT Pipeline completed!'
        }

        failure {
            echo 'ERROR: Pipeline failed!'
        }
    }
}
