
pipeline {
    agent any

    stages {
        stage('Start Selenium Grid') {
            steps {
                bat '''
                    docker compose up -d
                    if errorlevel 1 exit /b 1
                    timeout /t 15 /nobreak
                    docker ps -a
                    curl.exe -f http://localhost:4444/status
                    if errorlevel 1 (
                        docker logs selenium-hub
                        exit /b 1
                    )
                '''
            }
        }

        stage('Cross-Browser Testing') {
            parallel {
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
                }

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
                }
            }
        }
    }

   
post {
    always {
        archiveArtifacts(
            artifacts: 'chrome-workspace/target/surefire-reports/**,chrome-workspace/target/screenshots/**,firefox-workspace/target/surefire-reports/**,firefox-workspace/target/screenshots/**',
            allowEmptyArchive: true
        )

        bat 'docker ps -a'
       bat 'docker inspect --format "{{json .State.Health}}" selenium-crossbrowser-pipeline-selenium-hub-1'
        bat 'docker logs selenium-crossbrowser-pipeline-selenium-hub-1'
    }
}

}
