import sys
import json
import csv
import os


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

    # Extract dish information and write to CSV
    app_dir = os.path.dirname(os.path.abspath(__file__))
    output_path = os.path.join(app_dir, 'output.csv')
    with open(output_path, 'w', newline='') as csv_file:
        writer = csv.writer(csv_file)

        # Write the title row
        writer.writerow(['TITLE', title])

        # Write the description row
        writer.writerow(['DESCRIPTION', description])

        # Write the dish headers
        writer.writerow(['DISH', 'PRICE'])

        # Create a dictionary to keep track of dish counts and rows
        dish_counts = {}
        dish_rows = {}

        # Write the dish details
        for dish in dishes_list:
            name = dish.get('name', '')
            price = dish.get('price', '')

            # Update the dish count and price
            dish_key = f"{name}_{price}"
            if dish_key in dish_counts:
                dish_counts[dish_key] += 1
            else:
                dish_counts[dish_key] = 1

            # Update the dish rows with the latest occurrence
            dish_rows[dish_key] = [
                f"{name}{' x' + str(dish_counts[dish_key]) if dish_counts[dish_key] > 1 else ''}",
                format_price(price * dish_counts[dish_key])]

        # Write only the last occurrence of each dish
        for row in dish_rows.values():
            writer.writerow(row)

        # Write the total price row
        writer.writerow(['TOTAL', format_price(total_price)])

        # Thank the customer :)
        writer.writerow(['Thank you for your visit :)!'])

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
