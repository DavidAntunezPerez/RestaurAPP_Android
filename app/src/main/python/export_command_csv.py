import sys
import json
import pandas as pd
import os
import csv

def format_price(price):
    # Format price as money with the dollar sign at the end
    return f"${price:.2f}"


def main(file_path):
    # Read the JSON file
    with open(file_path, 'r') as json_file:
        json_data = json.load(json_file)

    # Extract information from the JSON data
    title = json_data.get('title', '')
    description = json_data.get('description', '')
    table_number = json_data.get('tableNumber', '')
    total_price = json_data.get('totalPrice', '')

    # Create a dictionary to store dish name counts and prices
    dish_data = {}

    # Iterate over dishes in the JSON data
    dishes_list = json_data.get('dishesList', [])
    for dish in dishes_list:
        name = dish.get('name', '')
        price = dish.get('price', '')

        # Check if the dish name already exists in the dictionary
        if name in dish_data:
            # Increment the dish count
            dish_data[name]['count'] += 1
            # Update the dish price
            dish_data[name]['price'] = format_price(
                float(dish_data[name]['price'][1:]) + float(price)
            )
        else:
            # Initialize the dish count and price
            dish_data[name] = {'count': 1, 'price': format_price(price)}

    # Create a DataFrame from the dish data
    df = pd.DataFrame([
        {'DISH': 'TITLE', 'PRICE': title},
        {'DISH': 'DESCRIPTION', 'PRICE': description},
        {'DISH': 'DISH', 'PRICE': 'PRICE'}
    ])

    # Append the modified dish names and prices to the DataFrame
    for name, data in dish_data.items():
        count = data['count']
        price = data['price']
        if count > 1:
            name = f"{name} x{count}"
        df = df.append({'DISH': name, 'PRICE': price}, ignore_index=True)

    df = df.append([
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

    # Print the extracted information
    print('Title:', title)
    print('Description:', description)
    print('Table Number:', table_number)
    print('Total Price:', format_price(total_price))

    # Return the path of the generated CSV file
    return output_path
