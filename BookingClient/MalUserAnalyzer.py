import numpy as np

path = '/Users/silvanzeller/Desktop/TCOMM/Master Thesis/BookingService/src/main/java/com/example/demo/mal_users.txt'

file = open(path, 'r')

mal_users_file = file.read()

all_mal_users = mal_users_file.split('---')

arr = np.zeros(shape=[1, 61])

for i in range (1, 61):
    no_mal_users = all_mal_users[i].split('#').__len__()
    arr[0, i] = no_mal_users

max_value = np.amax(arr)
min_value = np.amin(arr)
mean_value = np.mean(arr)
median_value = np.median(arr)

print('max   : '+str(max_value))
print('min   : '+str(min_value))
print('mean  : '+str(mean_value))
print('median: '+str(median_value))

print(arr)


