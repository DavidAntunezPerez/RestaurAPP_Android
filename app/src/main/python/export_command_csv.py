import sys
import json
import pandas as pd
import os
import csv


def format_price(price):
    # Format price as money with the dollar sign at the end
    return f"${price}"


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

    # Create a DataFrame to store the data
    data = {
        'DISH': [],
        'PRICE': []
    }

    # Extract dish information
    dish_counts = {}
    for dish in dishes_list:
        name = dish.get('name', '')
        price = dish.get('price', '')

        # Update the dish count and price
        dish_key = f"{name}_{price}"
        if dish_key in dish_counts:
            dish_counts[dish_key] += 1
        else:
            dish_counts[dish_key] = 1

        # Append the dish details to the DataFrame
        data['DISH'].append(f"{name}{' x' + str(dish_counts[dish_key]) if dish_counts[dish_key] > 1 else ''}")
        data['PRICE'].append(price * dish_counts[dish_key])

    # Append the total price row
    data['DISH'].append('TOTAL')
    data['PRICE'].append(total_price)

    # Create a DataFrame from the data
    df = pd.DataFrame(data)

    # Create a CSV file path
    app_dir = os.path.dirname(os.path.abspath(__file__))
    output_path = os.path.join(app_dir, 'output.csv')

    # Write the DataFrame to a CSV file
    df.to_csv(output_path, index=False, quoting=csv.QUOTE_NONNUMERIC)

    # Print the extracted information
    print('Title:', title)
    print('Description:', description)
    print('Table Number:', table_number)
    print('Total Price:', total_price)

    # Return the path of the generated CSV file
    return output_path


if __name__ == '__main__':
    # Retrieve the JSON file path from the command-line argument
    json_file_path = sys.argv[1]

    # Call the main function with the JSON file path and print the result
    print(main(json_file_path))
