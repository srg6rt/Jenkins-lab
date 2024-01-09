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
    master.vm.box = "ubuntu/jammy64"
    master.vm.network "public_network", :bridge => "enp0s8", ip: JNKS_STATIC_IP
    master.vm.provision :shell, :inline => "sudo sed -i 's/PasswordAuthentication no/PasswordAuthentication yes/g' /etc/ssh/sshd_config; sudo systemctl restart sshd;", run: "always"
    master.vm.synced_folder "jenkins_master_settings/", "/home/vagrant", owner: "vagrant", group: "vagrant" #, :mount_options => ['dmode=777,fmode=666']
    master.vm.provider :virtualbox do |v|
            
    # Run GUI VirtualBox
    #v.gui = true
      v.memory = 4048
      v.cpus = 2
    end

    master.vm.provision "shell", inline: <<-SHELL

      
      apt update -y
      echo "INSTALL WGET"
      apt install -y wget

      dpkg --configure -a


      echo "INSTALL Java"
      apt install -y openjdk-11-jdk

      # apt install -y java-11-openjdk-devel
      # export JAVA_HOME=$(dirname $(dirname $(readlink $(readlink $(which javac)))))
      # export PATH=$PATH:$JAVA_HOME/bin
      # export JRE_HOME=/usr/lib/jvm/jre
      # export CLASSPATH=.:$JAVA_HOME/jre/lib:$JAVA_HOME/lib:$JAVA_HOME/lib/tools.jar

      
      echo "Add Jenkins RPM repository"
  
      curl -fsSL https://pkg.jenkins.io/debian-stable/jenkins.io-2023.key | sudo tee /usr/share/keyrings/jenkins-keyring.asc > /dev/null
      echo deb [signed-by=/usr/share/keyrings/jenkins-keyring.asc] https://pkg.jenkins.io/debian-stable binary/ | sudo tee /etc/apt/sources.list.d/jenkins.list > /dev/null

      apt update -y


      echo "INSTALL Jenkins"
      apt install -y jenkins

      echo "INSTALL GIT"
      apt install -y git

      #apt install -y docker
      
      #echo "Skipping the initial setup"
      #echo 'JAVA_ARGS="-Djenkins.install.runSetupWizard=false"' >> /etc/default/jenkins

      #export JAVA_OPTS="JAVA_OPTS -Djava.awt.headless=true -Djenkins.install.runSetupWizard=false"
      # ?? 
      #echo 'JAVA_OPTS=-Djava.awt.headless=true -Djenkins.install.runSetupWizard=false' >> /etc/default/jenkins



      echo "Setting up users"
      rm -rf /var/lib/jenkins/init.groovy.d
      mkdir /var/lib/jenkins/init.groovy.d
      wget https://github.com/srg6rt/Jenkins-lab/blob/main/jenkins_master_settings/01_globalMatrixAuthorizationStrategy.groovy
      wget https://github.com/srg6rt/Jenkins-lab/blob/main/jenkins_master_settings/02_createAdminUser.groovy
      cp -v /home/vagrant/01_globalMatrixAuthorizationStrategy.groovy /var/lib/jenkins/init.groovy.d/
      cp -v /home/vagrant/02_createAdminUser.groovy /var/lib/jenkins/init.groovy.d/


      systemctl enable jenkins
      systemctl start jenkins.service

      sleep 1m

     

      echo "Installing jenkins plugins"
      JENKINSPWD=$(sudo cat /var/lib/jenkins/secrets/initialAdminPassword)
      rm -f jenkins_cli.jar.*
      wget -q http://localhost:8080/jnlpJars/jenkins-cli.jar

      
      list=$(python3 /home/vagrant/test.py)

      java -jar ./jenkins-cli.jar -auth admin:$JENKINSPWD -s http://localhost:8080 install-plugin $list

      echo "Restarting Jenkins"
      service jenkins restart

      sleep 1m

      echo "Restarting server"
      reboot


    SHELL

    master.vm.network "forwarded_port", guest: 8080, host: 8080
  end


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

    agent.vm.network "forwarded_port", guest: 8080, host: 8081
  end

end
