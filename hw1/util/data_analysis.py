import csv
from collections import defaultdict
from collections import OrderedDict

file = open("../client-part2/result.csv", "r")
data = list(csv.reader(file, delimiter=","))
file.close()

start_dict = defaultdict(int)
end_dict = defaultdict(int)

min_start = float('inf')
min_end = float('inf')

for row in range(len(data)):
  if (int(data[row][1])//1000) < min_start:
    min_start = int(data[row][1])//1000
  if (int(data[row][2])//1000) < min_end:
    min_end = int(data[row][2])//1000

for row in range(len(data)):
  start = int(data[row][1])//1000 - min_start
  end = int(data[row][2])//1000 - min_end

  start_dict[start] += 1
  end_dict[end] += 1

sum_start = 0
with open("start.csv", "w+") as start_file:
  start_writer = csv.writer(start_file)
  start_writer.writerow(["time", "amount of request", "throughput"])
  for key, value in start_dict.items():
    sum_start += value
    start_writer.writerow([key, value, sum_start/(key+1)])

sum_end = 0
with open("end.csv", "w+") as end_file:
  end_writer = csv.writer(end_file)
  end_writer.writerow(["time", "amount of request", "throughput"])
  for key, value in end_dict.items():
    sum_end += value
    end_writer.writerow([key, value, sum_end/(key+1)])


