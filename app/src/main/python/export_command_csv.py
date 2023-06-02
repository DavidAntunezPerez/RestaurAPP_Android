import sys
import json
import csv
import os


def main(file_path):
    # Read the JSON file
    with open(file_path, 'r') as json_file:
        json_data = json.load(json_file)

    # Extract information from the JSON data
    title = json_data.get('title', '')
    description = json_data.get('description', '')
    table_number = json_data.get('tableNumber', '')
    total_price = json_data.get('totalPrice', '')

    dishes_list = json_data.get('dishesList', [])
    # Extract dish information and write to CSV
    app_dir = os.path.dirname(os.path.abspath(__file__))
    output_path = os.path.join(app_dir, 'output.csv')
    with open(output_path, 'w', newline='') as csv_file:
        writer = csv.writer(csv_file)
        writer.writerow(['ID', 'IDRestaurant', 'Image', 'Name', 'Price'])
        for dish in dishes_list:
            dish_id = dish.get('id', '')
            restaurant_id = dish.get('idRestaurant', '')
            image = dish.get('image', '')
            name = dish.get('name', '')
            price = dish.get('price', '')

            writer.writerow([dish_id, restaurant_id, image, name, price])

    # Print the extracted information
    print('Title:', title)
    print('Description:', description)
    print('Table Number:', table_number)
    print('Total Price:', total_price)
    print('CSV file generated successfully.')


if __name__ == '__main__':
    # Retrieve the JSON file path from command-line argument
    json_file_path = sys.argv[1]

    # Call the main function with the JSON file path
    main(json_file_path)
