pipeline {
    agent any
    tools {
        maven 'maven_3_9_9'
    }
    stages {
        stage('Build Maven') {
            steps {
                checkout([
                    $class: 'GitSCM',
                    branches: [[name: '*/master']],
                    extensions: [],
                    userRemoteConfigs: [[url: 'https://github.com/VenkataKrishnaRepalle/ems-mybatis-liquibase']]
                ])

                bat 'mvn clean install'
            }
        }
//         stage('Build Docker Image') {
//             steps {
//                 script {
//                     bat 'docker build -t ems-mybatis-liquibase .'
//                 }
//             }
//         }
        stage('Deploy with Docker Compose') {
            steps {
                script {
                        withCredentials([string(credentialsId: 'DOCKER_PASSWORD', variable: 'docker_password')]) {
                        bat 'docker login -u rvkrishna13052001 -p %docker_password%'
                    }

//                     bat 'docker tag ems-mybatis-liquibase rvkrishna13052001/ems-mybatis-liquibase:latest'
//                     bat 'docker push rvkrishna13052001/ems-mybatis-liquibase:latest'
                    bat 'docker-compose down'
                    bat 'docker-compose up --build -d'
                }
            }
        }
    }
}
