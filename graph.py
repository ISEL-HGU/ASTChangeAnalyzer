import plotext as plx
import numpy as np

x = []
y1 = []
y2 = []
with open("Statistic.txt") as f:
#with open("../../../../../../../../../Statistic.txt") as f:
  lines = f.readlines()

for line in lines :
    if line.find("L Analyzed Change size: ") != -1 :
        pos = line.rindex("L Analyzed Change size: ")
        x.append(int(line[pos+24:]))
    if line.find("file level) size: ") != -1 :
        pos = line.rindex("file level) size: ")
        y1.append(int(line[pos+18:]))
    if line.find("core level) size: ") != -1 :
        pos = line.rindex("core level) size: ")
        y2.append(int(line[pos+18:]))

print("file level index\n")
print(y1)
print("\n")
print("hunk level index\n")
print(y2)
print("\n")
plx.title("Result Summary")
plx.scatter(x,y1)
plx.ylabel("blue : file level,  green : hunk level")
plx.scatter(x,y2)
plx.show()