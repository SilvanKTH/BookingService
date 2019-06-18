import urllib.parse
import urllib.request
import random
import time
import os
import shutil
import sys


ctr = 0
mal_ctr = 0


def incr_ctr():
    global ctr
    ctr = (ctr + 1) % sys.maxsize


def incr_mal_ctr():
    global mal_ctr
    mal_ctr = (mal_ctr + 1) % sys.maxsize


class BookingClient:
    def __init__(self, no_rooms, arr_date, no_nights, user_name, credit_card, cancel_likelihood):
        self.no_rooms = no_rooms
        self.arr_date = arr_date
        self.no_nights = no_nights
        self.user_name = user_name
        self.credit_card = credit_card
        self.cancel_likelihood = cancel_likelihood

        self.dep_date = arr_date + no_nights

        self.url_get_av_rooms = 'http://localhost:8080/available-rooms'
        self.url_reserve_rooms = 'http://localhost:8080/contact'
        self.url_conf_booking = 'http://localhost:8080/confirmation'

        self.booking_id = 0
        self.booking_date = 0
        self.cancel_latest = 0
        self.cancel_date = 0

    def post_request(self, url, values):
        data = urllib.parse.urlencode(values)
        data = data.encode('utf-8')
        req = urllib.request.Request(url, data)
        resp = urllib.request.urlopen(req)
        resp_str = str(resp.read())
        # print('HTTP RESPONSE: '+resp_str)
        return resp_str

    def get_dates_and_rooms(self, av_rooms_resp, no_rooms):
        av_rooms_resp = av_rooms_resp.split('<p')
        av_rooms_resp = av_rooms_resp[1].split(' ')
        arr_date = int(av_rooms_resp[2])
        dep_date = int(av_rooms_resp[4])
        no_av_rooms = int(av_rooms_resp[7])
        if (no_av_rooms >= no_rooms):
            no_av_rooms = no_rooms
        return arr_date, dep_date, no_av_rooms

    def make_reservation(self, reserve_rooms_str):
        success = False
        if (reserve_rooms_str.__contains__('The booking is being processed')):
            success = True
        return success

    def get_conf_and_booking_id(self, conf_booking_resp):
        if (conf_booking_resp.__contains__('Thanks for your booking')):
            conf_booking_resp = conf_booking_resp.split('<p')
            cancel_latest_arr = conf_booking_resp[4].split(' ')
            booking_id_arr = conf_booking_resp[5].split(' ')
            booking_date_array = conf_booking_resp[6].split(' ')
            self.cancel_latest = cancel_latest_arr[6]
            self.booking_id = booking_id_arr[9]
            self.booking_date = booking_date_array[5]
        else:
            self.cancel_latest = '0'
            self.booking_id = '0'

    def det_cancel_behaviour(self):
        cancels = False
        if (self.user_name.__contains__('malicious')):
            cancels = True
        else:
            random.seed(ctr)
            rand_no = random.randrange(0, 100)
            incr_ctr()
            if (self.cancel_likelihood >= rand_no):
                cancels = True
        return cancels

    def det_cancel_date(self):
        if (self.user_name.__contains__('malicious')): # only the case when user is malicious
            random.seed(mal_ctr)
            rand_no = random.randrange(0, 1)
            incr_mal_ctr()
            rand_no = 0
            self.cancel_date = int(self.cancel_latest) - rand_no
            if (self.user_name == 'malicious165'):
                print('cancel_latest: '+str(self.cancel_latest))
                print('cancel_date: '+str(self.cancel_date))
                print('booking_date: '+str(self.booking_date))
                print('booking_id: '+str(self.booking_id))
        elif (int(self.cancel_latest) - int(self.booking_date) > 1):
            random.seed(ctr)
            rand_no = random.randrange(int(self.booking_date) + 1, int(self.cancel_latest))
            incr_ctr()
            self.cancel_date = rand_no
        else:
            self.cancel_date = int(self.cancel_latest)

    def run_booking_client(self):

        values_get_av_rooms = {'rooms': self.no_rooms,
                               'arrival': self.arr_date,
                               'departure': self.no_nights}
        av_rooms_str = self.post_request(self.url_get_av_rooms, values_get_av_rooms)
        arr_date_temp, dep_date_temp, no_rooms_temp = self.get_dates_and_rooms(av_rooms_str, self.no_rooms)

        values_reserve_rooms = {'name': self.user_name,
                                'rooms': str(no_rooms_temp),
                                'arrival': str(arr_date_temp),
                                'departure': str(self.no_nights)}
        reserve_rooms_str = self.post_request(self.url_reserve_rooms, values_reserve_rooms)
        reserved = self.make_reservation(reserve_rooms_str)

        if (reserved):
            values_conf_booking = {'name': self.user_name,
                                   'creditcard': self.credit_card}
            booking_conf_str = self.post_request(self.url_conf_booking, values_conf_booking)
            self.get_conf_and_booking_id(booking_conf_str)

        cancels = self.det_cancel_behaviour()

        if (cancels and int(self.booking_id) > 0):
            self.det_cancel_date()
            filename = 'MalUsers/cancel_ids'+str(self.cancel_date)
            file = open(filename, 'a+')
            file.write(':' + str(self.booking_id))
            file.close()

        ### TEST PRINTOUTS
        #print('user will cancel at: ' + str(self.cancel_date))
        if (self.cancel_date > 0):
            print(self.user_name + ' will cancel at: ' + str(self.cancel_date))


class CancelClient:
    def __init__(self, filename):
        self.filename = filename

        self.url_conf_cancel = 'http://localhost:8080/cancel-confirmation'

        self.cancel_list = []

    def post_request(self, url, values):
        data = urllib.parse.urlencode(values)
        data = data.encode('utf-8')
        req = urllib.request.Request(url, data)
        resp = urllib.request.urlopen(req)
        resp_str = str(resp.read())
        # print('HTTP RESPONSE: '+resp_str)
        return resp_str

    def read_ids_from_file(self):
        try:
            file = open(self.filename, 'r')
            cancel_ids_str = file.read()
            file.close()
            print(cancel_ids_str)
            self.cancel_list = cancel_ids_str.split(':')
        except Exception as e:
            print(e)

    def run_cancel_client(self):
        self.read_ids_from_file()
        if (self.cancel_list.__len__() > 0):
            for booking_id in self.cancel_list:
                values_cancel_res = {'id': booking_id}
                cancel_conf_str = self.post_request(self.url_conf_cancel, values_cancel_res)
                if (cancel_conf_str.__contains__('We hope to seeing you again soon!')):
                    print('Cancellation with ID ' + booking_id + ' succeeded! ')
                else:
                    print('Cancellations with ID ' + booking_id + ' failed')
        else:
            print('No cancellations in this period!')


class ClientCreator:
    def __init__(self, no_planned_users, no_normal_users, no_spontaneous_users, no_malicious_users):
        self.no_planned_users = no_planned_users
        self.no_normal_users = no_normal_users
        self.no_spontaneous_users = no_spontaneous_users
        self.no_malicious_users = no_malicious_users

        self.min_days_in_advance = 2
        self.min_days_in_advance_normal = 7
        self.min_days_in_advance_planned = 14
        self.max_days_in_advance = 30

        self.benign_cancel_likelihood = 10
        self.malicious_cancel_likelihood = 100

        self.user_appendix_factor = 5

    def planned_user_creator(self):
        for x in range (0, self.no_planned_users):
            random.seed(ctr)
            no_rooms = random.randrange(1, 4)
            incr_ctr()
            random.seed(ctr)
            arr_date = random.randrange(self.min_days_in_advance_planned, self.max_days_in_advance)
            incr_ctr()
            random.seed(ctr)
            no_nights = random.randrange(1, 7)
            incr_ctr()
            random.seed(ctr)
            user_appendix = random.randrange(0, self.no_planned_users * self.user_appendix_factor)
            incr_ctr()
            user_name = 'planned'+str(user_appendix)
            credit_card = '1234567890'
            cancel_likelihood = self.benign_cancel_likelihood
            user = BookingClient(no_rooms, arr_date, no_nights, user_name, credit_card, cancel_likelihood)
            user.run_booking_client()

    def normal_user_creator(self):
        for x in range (0, self.no_normal_users):
            random.seed(ctr)
            no_rooms = random.randrange(1, 4)
            incr_ctr()
            random.seed(ctr)
            arr_date = random.randrange(self.min_days_in_advance_normal, self.min_days_in_advance_planned)
            incr_ctr()
            random.seed(ctr)
            no_nights = random.randrange(1, 7)
            incr_ctr()
            random.seed(ctr)
            user_appendix = random.randrange(0, self.no_planned_users * self.user_appendix_factor)
            incr_ctr()
            user_name = 'normal'+str(user_appendix)
            credit_card = '5432112345'
            cancel_likelihood = self.benign_cancel_likelihood
            user = BookingClient(no_rooms, arr_date, no_nights, user_name, credit_card, cancel_likelihood)
            user.run_booking_client()

    def spontaneous_user_creator(self):
        for x in range (0, self.no_normal_users):
            random.seed(ctr)
            no_rooms = random.randrange(1, 4)
            incr_ctr()
            random.seed(ctr)
            arr_date = random.randrange(self.min_days_in_advance, self.min_days_in_advance_normal)
            incr_ctr()
            random.seed(ctr)
            no_nights = random.randrange(1, 7)
            incr_ctr()
            random.seed(ctr)
            user_appendix = random.randrange(0, self.no_planned_users * self.user_appendix_factor)
            incr_ctr()
            user_name = 'spontaneous'+str(user_appendix)
            credit_card = '3761389317'
            cancel_likelihood = self.benign_cancel_likelihood
            user = BookingClient(no_rooms, arr_date, no_nights, user_name, credit_card, cancel_likelihood)
            user.run_booking_client()

    def malicious_user_creator(self):
        for x in range (0, self.no_malicious_users):
            no_rooms = 4
            random.seed(mal_ctr)
            arr_date = random.randrange(self.min_days_in_advance, self.max_days_in_advance)
            incr_mal_ctr()
            random.seed(mal_ctr)
            no_nights = random.randrange(1, 7)
            incr_mal_ctr()
            random.seed(mal_ctr)
            user_appendix = random.randrange(0, self.no_malicious_users * self.user_appendix_factor)
            incr_mal_ctr()
            user_name = 'malicious'+str(user_appendix)
            credit_card = '6666666666'
            cancel_likelihood = self.malicious_cancel_likelihood
            user = BookingClient(no_rooms, arr_date, no_nights, user_name, credit_card, cancel_likelihood)
            user.run_booking_client()

    def run_user_creator(self):
        self.planned_user_creator()
        self.normal_user_creator()
        self.spontaneous_user_creator()
        self.malicious_user_creator()


def main():
    start_time = int(round(time.time() * 1000))
    local_ctr = 1
    av_users_per_benign_group = 40
    av_mal_users = 40
    variance = 20
    mal_variance = 20
    simulation_period_in_min = 60
    path = '/Users/silvanzeller/Desktop/TCOMM/Master Thesis/BookingService/BookingClient/MalUsers'
    try:
        os.mkdir(path)
    except Exception as e:
        print(e)

    random.seed(ctr)
    no_planned = no_normal = no_spontaneous = \
        random.randrange(av_users_per_benign_group - variance, av_users_per_benign_group + variance)
    incr_ctr()

    random.seed(mal_ctr)
    no_malicious = random.randrange(av_mal_users - mal_variance, av_mal_users + mal_variance)
    incr_mal_ctr()
    #no_malicious = 0 ### UNCOMMENT FOR BENCHMARKING

    print('no of users = '+str((no_planned * 3) + no_malicious))

    creator = ClientCreator(no_planned, no_normal, no_spontaneous, no_malicious)
    creator.run_user_creator()
    stop_time = (int(round(time.time() * 1000)))
    diff_in_sec = (stop_time - start_time) / 1000
    sleep_time = 60 - diff_in_sec
    print(sleep_time)
    time.sleep(sleep_time)
    while (local_ctr < simulation_period_in_min + 1):
        start_time = int(round(time.time() * 1000))
        filename = '/cancel_ids'+str(local_ctr)
        print('searching for filename: '+filename)
        cancel = CancelClient(path+filename)
        cancel.run_cancel_client()

        random.seed(ctr)
        no_planned = no_normal = no_spontaneous = \
            random.randrange(av_users_per_benign_group - variance, av_users_per_benign_group + variance)
        incr_ctr()

        random.seed(mal_ctr)
        no_malicious = random.randrange(av_mal_users - mal_variance, av_mal_users + mal_variance)
        incr_mal_ctr()
        #no_malicious = 0 ### UNCOMMENT FOR BENCHMARKING

        print('no of users = ' + str((no_planned * 3) + no_malicious))

        creator = ClientCreator(no_planned, no_normal, no_spontaneous, no_malicious)
        creator.run_user_creator()
        local_ctr = local_ctr + 1
        stop_time = (int(round(time.time() * 1000)))
        diff_in_sec = (stop_time - start_time) / 1000
        sleep_time = 60 - diff_in_sec
        print(sleep_time)
        time.sleep(sleep_time)
    shutil.rmtree(path, ignore_errors=True)


if __name__ == '__main__':
    main()




