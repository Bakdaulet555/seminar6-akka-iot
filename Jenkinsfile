pipeline {
    agent any
    environment {
        JAVA_HOME = '/usr/lib/jvm/java-11-openjdk-amd64'
        PATH = "${JAVA_HOME}/bin:${env.PATH}"
    }

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
