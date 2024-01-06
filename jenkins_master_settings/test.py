
with open('/home/vagrant/jenkins_plugins.txt') as f:
    lines = f.readlines()
    f.close()

f = open("/home/vagrant/output.txt", "w")
bash_variable = ""
for i in lines:
    f.write(i.strip()+" ")
    bash_variable = bash_variable + i.strip()+" "
f.close()
print(bash_variable)




#with open('/home/vagrant/output.txt', 'wb') as out:
#    for frame in frames:
#        out.write(' '.join(str(num) for num in frame))
#        out.write('\r\n')
