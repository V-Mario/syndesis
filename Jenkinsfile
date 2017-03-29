def mavenVersion='3.3.9'

slave {
    withOpenshift {
            //Comment out until pvc issues are resolved
            //withMaven(mavenImage: "maven:${mavenVersion}", serviceAccount: "jenkins", mavenRepositoryClaim: "m2-local-repo") {
              withMaven(mavenImage: "maven:${mavenVersion}", serviceAccount: "jenkins") {
                inside {
                    def testingNamespace = generateProjectName()

                    checkout scm

                    stage 'Build'
                    container(name: 'maven') {
                        sh "mvn clean install fabric8:build -Pci -Duser.home=/home/jenkins"
                    }

                    stage 'System Tests'
                    test(component: 'ipaas-rest', namespace: "${testingNamespace}", serviceAccount: 'jenkins')
                 }

        }
    }
}
