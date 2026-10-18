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
    
        stage('Performance Testing') {
            steps {
                sh '''
                    mkdir -p target/jmeter
                    rm -f target/jmeter/jenkins-results.jtl

                    /opt/apache-jmeter-5.6.3/bin/jmeter -n \
                      -t src/test/jmeter/telemetry-load.jmx \
                      -Jhost=host.docker.internal \
                      -Jport=8081 \
                      -l target/jmeter/jenkins-results.jtl

                    python3 - <<'PYCODE'
import csv

path = "target/jmeter/jenkins-results.jtl"

with open(path, newline="") as f:
    rows = list(csv.DictReader(f))

if not rows:
    raise SystemExit("ERROR: JMeter returned no results")

errors = sum(
    row.get("success", "").lower() != "true"
    for row in rows
)

rate = errors * 100 / len(rows)

print(f"Requests: {len(rows)}")
print(f"Errors: {errors}")
print(f"Error rate: {rate:.2f}%")

if rate > 25:
    raise SystemExit("FAILURE: Error rate exceeds 25%")
PYCODE
                '''
            }
            post {
                always {
                    archiveArtifacts allowEmptyArchive: true,
                        artifacts: 'target/jmeter/*.jtl'
                }
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
