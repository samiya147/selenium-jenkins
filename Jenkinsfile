
pipeline {
    agent any

    stages {

        // STEP 1: Start Selenium Grid using Docker
        stage('Start Selenium Grid') {
            steps {
                bat '''
                    docker compose up -d
                    if errorlevel 1 exit /b 1

                    docker ps -a
                '''

                // Wait for Selenium Grid to become ready
                script {
                    retry(12) {
                        sleep time: 5, unit: 'SECONDS'

                        bat '''
                            curl.exe -fsS http://localhost:4444/status > grid-status.json
                            if errorlevel 1 exit /b 1
                        '''
                    }
                }

                echo 'Selenium Grid is responding.'
            }
        }

        // STEP 2: Run Chrome and Firefox tests simultaneously
        stage('Cross-Browser Testing') {

            parallel {

                // CHROME BRANCH
                stage('Chrome Tests') {
                    steps {
                        dir('chrome-workspace') {

                            checkout scm

                            bat '''
                                set "JAVA_HOME=C:\\Program Files\\java\\jdk-25"
                                set "PATH=%JAVA_HOME%\\bin;%PATH%"

                                call "C:\\Users\\samiya\\Downloads\\apache-maven-3.10.0-bin\\apache-maven-3.10.0\\bin\\mvn.cmd" clean "-Dtest=StayEaseBookingTest#testHotelBookingInChrome" test

                                if errorlevel 1 exit /b 1
                            '''
                        }
                    }

                    post {
                        always {
                            junit(
                                testResults: 'chrome-workspace/target/surefire-reports/*.xml',
                                allowEmptyResults: true
                            )
                        }
                    }
                }

                // FIREFOX BRANCH
                stage('Firefox Tests') {
                    steps {
                        dir('firefox-workspace') {

                            checkout scm

                            bat '''
                                set "JAVA_HOME=C:\\Program Files\\java\\jdk-25"
                                set "PATH=%JAVA_HOME%\\bin;%PATH%"

                                call "C:\\Users\\samiya\\Downloads\\apache-maven-3.10.0-bin\\apache-maven-3.10.0\\bin\\mvn.cmd" clean "-Dtest=StayEaseBookingTest#testHotelBookingInFirefox" test

                                if errorlevel 1 exit /b 1
                            '''
                        }
                    }

                    post {
                        always {
                            junit(
                                testResults: 'firefox-workspace/target/surefire-reports/*.xml',
                                allowEmptyResults: true
                            )
                        }
                    }
                }
            }
        }
    }

    // STEP 3: Collect reports and Docker logs after the pipeline
    post {
        always {
            archiveArtifacts(
                artifacts: 'chrome-workspace/target/surefire-reports/**,chrome-workspace/target/screenshots/**,firefox-workspace/target/surefire-reports/**,firefox-workspace/target/screenshots/**',
                allowEmptyArchive: true
            )

            bat 'docker ps -a'

            bat '''
                docker inspect --format "{{json .State.Health}}" selenium-crossbrowser-pipeline-selenium-hub-1
                if errorlevel 1 exit /b 0
            '''

            bat '''
                docker logs selenium-crossbrowser-pipeline-selenium-hub-1
                if errorlevel 1 exit /b 0
            '''
        }
    }
}
