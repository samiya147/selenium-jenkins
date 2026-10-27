
pipeline {
    agent any

    stages {
        stage('Cross-Browser Testing') {
            steps {
                bat '''
                    set "JAVA_HOME=C:\\Program Files\\java\\jdk-25"
                    set "PATH=%JAVA_HOME%\\bin;%PATH%"

                    call "%USERPROFILE%\\Downloads\\apache-maven-3.10.0-bin\\apache-maven-3.10.0\\bin\\mvn.cmd" -f "C:\\Users\\samiya\\Downloads\\selenium-jenkins\\pom.xml" clean test
                    set "TEST_RESULT=%ERRORLEVEL%"

                    if not exist target mkdir target
                    if exist "C:\\Users\\samiya\\Downloads\\selenium-jenkins\\target\\surefire-reports" (
                        xcopy /E /I /Y "C:\\Users\\samiya\\Downloads\\selenium-jenkins\\target\\surefire-reports" target\\surefire-reports
                    )
                    if exist "C:\\Users\\samiya\\Downloads\\selenium-jenkins\\target\\screenshots" (
                        xcopy /E /I /Y "C:\\Users\\samiya\\Downloads\\selenium-jenkins\\target\\screenshots" target\\screenshots
                    )

                    exit /b %TEST_RESULT%
                '''
            }
        }
    }

    post {
        always {
            archiveArtifacts artifacts: 'target/**',
                             allowEmptyArchive: true

            bat '''
                if exist target\\screenshots rmdir /s /q target\\screenshots
                if exist target\\surefire-reports rmdir /s /q target\\surefire-reports
            '''
            echo 'Cleanup completed. Archived reports are retained in Jenkins.'
        }
    }
}
