import numpy as np

path = '/Users/silvanzeller/Desktop/TCOMM/Master Thesis/BookingService/src/main/java/com/example/demo/mal_users.txt'
#path = '/Users/silvanzeller/Desktop/TCOMM/Master Thesis/BookingService/src/main/java/com/example/demo/mal_users_reference.txt'

file = open(path, 'r')

mal_users_file = file.read()

all_mal_users = mal_users_file.split('---')

arr = np.zeros(shape=[1, 61])

for i in range (1, 61):
    no_mal_users = all_mal_users[i].split('#').__len__()
    arr[0, i] = no_mal_users


def sum_of_occ(arr_, i):
    ctr = 0
    len_ = arr_.size
    for j in range (0, len_):
        if arr_[0, j] == i:
            ctr = ctr + 1
    return ctr


def make_pdf(arr_):
    arr_ = arr_.astype(int)
    print(arr_)
    print(arr_[0, 0])
    max_value_ = np.amax(arr_)
    max_value_ = int(max_value_)
    cdf = np.zeros(shape=[1, max_value_ + 1])
    for i in range (0, max_value_ + 1):
        cdf[0, i] = sum_of_occ(arr_, i)
    return cdf.astype(int)


max_value = np.amax(arr)
min_value = np.amin(arr)
mean_value = np.mean(arr)
median_value = np.median(arr)
std_dev = np.std(arr)
pdf = make_pdf(arr)

print('max   : '+str(max_value))
print('min   : '+str(min_value))
print('mean  : '+str(mean_value))
print('median: '+str(median_value))
print('std   : '+str(std_dev))
print('pdf   : '+str(pdf))

