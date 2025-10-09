pipeline {
    agent any

    tools {
        jdk 'jdk-21'
        maven 'maven3'
    }

    stages {

        // ------------------ Stage 1: Checkout ------------------
        stage('Checkout') {
            steps {
                git branch: 'master',
                    credentialsId: 'f9172797-3192-474d-a03c-bc5a15f2ed32',
                    url: 'https://github.com/vebhav24/cicd.git'
            }
        }

        // ------------------ Stage 2: Build ------------------
        stage('Build') {
            steps {
                dir('myapp') {
                    bat 'mvn clean package -DskipTests'
                }
            }
        }

        // ------------------ Stage 3: Unit Tests ------------------
        stage('Test') {
            steps {
                dir('myapp') {
                    bat 'mvn test'
                }
                // Archive test results for Jenkins UI
                junit 'myapp/target/surefire-reports/*.xml'
            }
        }

        // ------------------ Stage 4: Deploy to Tomcat ------------------
        stage('Deploy to Tomcat') {
            steps {
                script {
                    // ⚙️ Adjust for your local Tomcat setup
                    env.TOMCAT_HOME = 'C:\\tomcat\\apache-tomcat-10.1.31'
                    env.TOMCAT_SERVICE = 'Tomcat10' // exact Windows service name
                }

                echo '🚀 Deploying WAR to Tomcat manually...'

                bat '''
                    echo Checking Tomcat status...
                    sc query "%TOMCAT_SERVICE%" | find "RUNNING" >nul && (
                        echo Stopping Tomcat service...
                        net stop "%TOMCAT_SERVICE%"
                    ) || echo Tomcat not running.

                    echo Cleaning old deployment...
                    del /Q "%TOMCAT_HOME%\\webapps\\myapp.war" 2>nul
                    rmdir /S /Q "%TOMCAT_HOME%\\webapps\\myapp" 2>nul

                    echo Copying new WAR file...
                    copy "%WORKSPACE%\\myapp\\target\\myapp.war" "%TOMCAT_HOME%\\webapps\\myapp.war" /Y

                    echo Starting Tomcat service...
                    net start "%TOMCAT_SERVICE%"
                '''

                echo 'Waiting for Tomcat to deploy WAR...'
                retry(10) {
                    sleep 5
                    bat '''
                        powershell -NoProfile -Command ^
                            "try { $r = Invoke-WebRequest 'http://localhost:8080/myapp' -UseBasicParsing -TimeoutSec 3; ^
                                   if ($r.StatusCode -ge 200 -and $r.StatusCode -lt 500) { exit 0 } else { exit 1 } } ^
                             catch { exit 1 }"
                    '''
                }
            }
        }

        // ------------------ Stage 5: Verify Deployment ------------------
        stage('Verify Deployment') {
            steps {
                echo '🔍 Verifying that the app is deployed and running...'
                bat 'curl -I http://localhost:8080/myapp'
            }
        }

        // ------------------ Stage 6: UI Tests (Optional Post-Deploy) ------------------
        stage('UI Tests (Post-Deploy)') {
            steps {
                dir('myapp') {
                    // Run UI tests only if app is reachable
                    bat 'mvn -P ui-tests test -DbaseUrl=http://localhost:8080/myapp'
                }
            }
        }
    }

    post {
        success {
            echo '✅ CI/CD pipeline executed successfully — Build, Test, Deploy complete.'
        }
        failure {
            echo '❌ Pipeline failed. Please check the Jenkins console log for details.'
        }
    }
}
