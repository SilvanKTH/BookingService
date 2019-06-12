import urllib.parse
import urllib.request
import random
import time
import os
import shutil


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
            #print('Reservation succeeded!')
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
        #return cancel_latest, booking_id

    def det_cancel_behaviour(self):
        cancels = False
        rand_no = random.randrange(0, 100)
        if (self.cancel_likelihood >= rand_no):
            cancels = True
        return cancels

    def det_cancel_date(self):
        if (self.cancel_likelihood == 100):
            rand_no = random.randrange(0, 1)
            self.cancel_date = int(self.cancel_latest) - rand_no
        elif (int(self.cancel_latest) - int(self.booking_date) > 1):
            rand_no = random.randrange(int(self.booking_date) + 1, int(self.cancel_latest))
            self.cancel_date = rand_no
        else:
            self.cancel_date = int(self.cancel_latest)

    def run_booking_client(self):
        ### TEST PRINTOUTS
        #print('user name: ' + self.user_name)
        #print('arrival date: ' + str(self.arr_date))
        #print('departure date: ' + str(self.dep_date))
        #print('number of nights: ' + str(self.no_nights))
        #print('number of rooms: ' + str(self.no_rooms))

        values_get_av_rooms = {'rooms': self.no_rooms,
                               'arrival': self.arr_date,
                               'departure': self.no_nights}
        av_rooms_str = self.post_request(self.url_get_av_rooms, values_get_av_rooms)
        arr_date_temp, dep_date_temp, no_rooms_temp = self.get_dates_and_rooms(av_rooms_str, self.no_rooms)

        ### TEST PRINTOUTS
        #print('user name: ' + self.user_name)
        #print('arrival date: ' + str(arr_date_temp))
        #print('departure date: ' + str(dep_date_temp))
        #print('number of nights: ' + str(self.no_nights))
        #print('number of rooms: ' + str(self.no_rooms))

        values_reserve_rooms = {'name': self.user_name,
                                'rooms': str(no_rooms_temp),
                                'arrival': str(arr_date_temp),
                                'departure': str(self.no_nights)}
        reserve_rooms_str = self.post_request(self.url_reserve_rooms, values_reserve_rooms)
        reserved = self.make_reservation(reserve_rooms_str)

        ### TEST PRINTOUTS
        #print('reservation status: ' + str(reserved))

        if (reserved):
            values_conf_booking = {'name': self.user_name,
                                   'creditcard': self.credit_card}
            booking_conf_str = self.post_request(self.url_conf_booking, values_conf_booking)
            self.get_conf_and_booking_id(booking_conf_str)

        cancels = self.det_cancel_behaviour()

        ### TEST PRINTOUTS
        #print('user will cancel: ' + str(cancels))

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
        self.min_days_in_advance_planned = 30
        self.max_days_in_advance = 60

        self.benign_cancel_likelihood = 10
        self.malicious_cancel_likelihood = 100

    def planned_user_creator(self):
        for x in range (0, self.no_planned_users):
            no_rooms = random.randrange(1, 4)
            arr_date = random.randrange(self.min_days_in_advance_planned, self.max_days_in_advance)
            no_nights = random.randrange(1, 7)
            user_appendix = random.randrange(0, self.no_planned_users * 10)
            user_name = 'planned'+str(user_appendix)
            credit_card = '1234567890'
            cancel_likelihood = self.benign_cancel_likelihood
            user = BookingClient(no_rooms, arr_date, no_nights, user_name, credit_card, cancel_likelihood)
            user.run_booking_client()

    def normal_user_creator(self):
        for x in range (0, self.no_normal_users):
            no_rooms = random.randrange(1, 4)
            arr_date = random.randrange(self.min_days_in_advance_normal, self.min_days_in_advance_planned)
            no_nights = random.randrange(1, 7)
            user_appendix = random.randrange(0, self.no_planned_users * 10)
            user_name = 'normal'+str(user_appendix)
            credit_card = '5432112345'
            cancel_likelihood = self.benign_cancel_likelihood
            user = BookingClient(no_rooms, arr_date, no_nights, user_name, credit_card, cancel_likelihood)
            user.run_booking_client()

    def spontaneous_user_creator(self):
        for x in range (0, self.no_normal_users):
            no_rooms = random.randrange(1, 4)
            arr_date = random.randrange(self.min_days_in_advance, self.min_days_in_advance_normal)
            no_nights = random.randrange(1, 7)
            user_appendix = random.randrange(0, self.no_planned_users * 10)
            user_name = 'spontaneous'+str(user_appendix)
            credit_card = '3761389317'
            cancel_likelihood = self.benign_cancel_likelihood
            user = BookingClient(no_rooms, arr_date, no_nights, user_name, credit_card, cancel_likelihood)
            user.run_booking_client()

    def malicious_user_creator(self):
        for x in range (0, self.no_malicious_users):
            no_rooms = 4
            arr_date = random.randrange(self.min_days_in_advance, self.min_days_in_advance_normal)
            no_nights = random.randrange(1, 7)
            user_appendix = random.randrange(0, self.no_malicious_users * 10)
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
    ctr = 1
    simulation_period_in_min = 15
    path = '/Users/silvanzeller/Desktop/TCOMM/Master Thesis/BookingService/Test'
    try:
        os.mkdir(path)
    except Exception as e:
        print(e)
    creator = ClientCreator(60, 60, 60, 20)
    creator.run_user_creator()
    stop_time = (int(round(time.time() * 1000)))
    diff_in_sec = (stop_time - start_time) / 1000
    sleep_time = 60 - diff_in_sec
    print(sleep_time)
    time.sleep(sleep_time)
    while (ctr < simulation_period_in_min + 1):
        start_time = int(round(time.time() * 1000))
        filename = '/cancel_ids'+str(ctr)
        print('searching for filename: '+filename)
        cancel = CancelClient(path+filename)
        cancel.run_cancel_client()
        creator = ClientCreator(60, 60, 60, 20)
        creator.run_user_creator()
        ctr = ctr + 1
        stop_time = (int(round(time.time() * 1000)))
        diff_in_sec = (stop_time - start_time) / 1000
        sleep_time = 60 - diff_in_sec
        print(sleep_time)
        time.sleep(sleep_time)
    shutil.rmtree(path, ignore_errors=True)


if __name__ == '__main__':
    main()




