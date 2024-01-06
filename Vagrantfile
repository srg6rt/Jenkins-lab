# -*- mode: ruby -*-
# vi: set ft=ruby :

# Run VirtualBox with administrator privileges and make shure you install VirtualBox Extension Pack in VirtualBox.
# Also run command in cmd "vagrant plugin install vagrant-vbguest"
# Need for vm.synced_folder

JNKS_STATIC_IP          =   "192.168.1.175"
WNDS_AGNT_STATIC_IP     =   "192.168.1.176"
LNX_AGNT_STATIC_IP      =   "192.168.1.177"


Vagrant.configure("2") do |config|

  # Master server
  config.vm.define "jenkins-master" do |master|
    master.vm.box = "eurolinux-vagrant/centos-stream-9"
    master.vm.box_version = "9.0.38"

    
    master.vm.hostname = "jenkins-master"
    master.vm.network "public_network", :bridge => "eth0", ip: JNKS_STATIC_IP
    master.vm.provider "virtualbox" do |vb|
      vb.memory = 2048
    end

    master.vm.provision "shell", inline: <<-SHELL

      yum update -y

      echo "INSTALL WGET"
      yum install -y wget

      echo "INSTALL Java"
      yum install -y java-11-openjdk-devel
      export JAVA_HOME=$(dirname $(dirname $(readlink $(readlink $(which javac)))))
      export PATH=$PATH:$JAVA_HOME/bin
      export JRE_HOME=/usr/lib/jvm/jre
      export CLASSPATH=.:$JAVA_HOME/jre/lib:$JAVA_HOME/lib:$JAVA_HOME/lib/tools.jar

      
      echo "Add Jenkins RPM repository"
      wget -O /etc/yum.repos.d/jenkins.repo http://pkg.jenkins-ci.org/redhat/jenkins.repo
      rpm --import https://pkg.jenkins.io/redhat-stable/jenkins.io-2023.key

      echo "INSTALL Jenkins"
      yum -y install jenkins

      echo "INSTALL GIT"
      yum install -y git

      #yum install -y docker


      # echo "Setting up users"
      # sudo rm -rf /var/lib/jenkins/init.groovy.d
      # sudo mkdir /var/lib/jenkins/init.groovy.d
      # sudo cp -v /home/vagrant/01_globalMatrixAuthorizationStrategy.groovy /var/lib/jenkins/init.groovy.d/
      # sudo cp -v /home/vagrant/02_createAdminUser.groovy /var/lib/jenkins/init.groovy.d/


      systemctl enable jenkins
      systemctl start jenkins

      sleep 1m

      # echo "Skipping the initial setup"
      # echo 'JAVA_ARGS="-Djenkins.install.runSetupWizard=false"' >> /etc/default/jenkins

      # echo "Installing jenkins plugins"
      # JENKINSPWD=$(sudo cat /var/lib/jenkins/secrets/initialAdminPassword)
      # rm -f jenkins_cli.jar.*
      # wget -q http://localhost:8080/jnlpJars/jenkins-cli.jar
      # while IFS= read -r line
      # do
      #   list=$list' '$line
      # done < /home/vagrant/jenkins-plugins.txt
      # java -jar ./jenkins-cli.jar -auth admin:$JENKINSPWD -s http://localhost:8080 install-plugin $list

      # echo "Restarting Jenkins"
      # sudo service jenkins restart

      # sleep 1m


    SHELL

    master.vm.network "forwarded_port", guest: 8080, host: 8080
  end

  # Windows agent
  # config.vm.define "agent-windows" do |agent|
  #   agent.vm.box = "hashicorp/windows-2022"
  #   agent.vm.hostname = "jenkins-agent-windows"
  #   agent.vm.network "public_network", :bridge => "eth0", ip: WNDS_AGNT_STATIC_IP
  #   agent.vm.provider "virtualbox" do |vb|
  #     vb.memory = 2048
  #   end

  #   agent.vm.provision "shell", inline: <<-SHELL
  #     Add-WindowsFeature Web-Server
  #     Install-Module Jenkins
  #     Jenkins::AgentInstall
  #   SHELL

  #   agent.vm.network "forwarded_port", guest: 8080, host: 8080
  # end

  # Linux agent
  config.vm.define "agent-linux" do |agent|
    agent.vm.box = "eurolinux-vagrant/centos-stream-9"
 
    agent.vm.hostname = "jenkins-agent-linux"
    agent.vm.network "public_network", :bridge => "eth0", ip: LNX_AGNT_STATIC_IP
    agent.vm.provider "virtualbox" do |vb|
      vb.memory = 2048
    end

    agent.vm.provision "shell", inline: <<-SHELL
      yum update -y
      yum install -y jenkins-agent
      systemctl enable jenkins-agent
      systemctl start jenkins-agent
    SHELL

    agent.vm.network "forwarded_port", guest: 8080, host: 8080
  end
end
