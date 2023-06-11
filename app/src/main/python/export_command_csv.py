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

    # Create a list of dictionaries for dish details
    dish_data = []
    dishes_list = json_data.get('dishesList', [])
    for dish in dishes_list:
        name = dish.get('name', '')
        price = dish.get('price', '')
        dish_data.append({
            'DISH': name,
            'PRICE': format_price(price)
        })

    # Create a DataFrame from the dish data
    df = pd.DataFrame([
        {'DISH': 'TITLE', 'PRICE': title},
        {'DISH': 'DESCRIPTION', 'PRICE': description},
        {'DISH': 'DISH', 'PRICE': 'PRICE'}
    ] + dish_data + [
        {'DISH': 'TOTAL', 'PRICE': format_price(total_price)},
        {'DISH': 'Thank you for your visit :)', 'PRICE': ''}
    ])

    # Write the DataFrame to a CSV file
    app_dir = os.path.dirname(os.path.abspath(__file__))
    output_path = os.path.join(app_dir, 'output.csv')

    # Create a temporary DataFrame without header
    df_temp = df[1:]

    # Save the temporary DataFrame to CSV without header
    df_temp.to_csv(output_path, index=False, header=False)

    # Read the temporary CSV file
    with open(output_path, 'r') as temp_file:
        csv_data = temp_file.read()

    # Add the modified header to the CSV data
    csv_data_with_header = f"TITLE,\"{title}\"\n" + csv_data

    # Write the CSV data with modified header to the output file
    with open(output_path, 'w') as final_file:
        final_file.write(csv_data_with_header)

    # Print the extracted information
    print('Title:', title)
    print('Description:', description)
    print('Table Number:', table_number)
    print('Total Price:', format_price(total_price))

    # Return the path of the generated CSV file
    return output_path