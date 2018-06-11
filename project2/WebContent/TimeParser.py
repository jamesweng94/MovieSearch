import json

def main():
    json_filename = input("Enter Filename: ")
    
    TS_total = 0
    TJ_total = 0

    queries_count = 0
    
    with open(json_filename, 'r') as json_file:
        json_data = json_file.readlines()
        for data in json_data:
            data = data.strip()
            d = json.loads(data)
            TS_total += d["TS"]
            TJ_total += d["TJ"]
            queries_count += 1

    print("Average TS = ", str(TS_total/queries_count))
    print("Average TJ = ", str(TJ_total/queries_count))
            
            

if __name__ == '__main__':
    main()
