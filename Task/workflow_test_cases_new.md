For this test, there are no directories or files where the task should be completed in. You can create any structure you find suitable, as long as the tasks are completed as described.

If you need to make an assumption about a task, please document it in the code comments or the [assumptions.md](assumptions.md) file.

# Internal Supply Chain Management Test Cases
**Notes:** Use PostgresSQL as a database (how? pls send help). Table structures:

- **employee**
    - `id` (Primary Key, Integer, Auto-increment)
    - `username` (String, Unique, Not Null)
    - `password_hash` (String, Not Null)
    - `email` (String, Unique, Not Null)
    - `role` (String, e.g., 'staff', 'manager', 'admin')
    - `created_at` (Timestamp)
    - `updated_at` (Timestamp)

- **inventory**
    - `id` (Primary Key, Integer, Auto-increment)
    - `item_name` (String, Not Null)
    - `description` (Text)
    - `quantity` (Integer, Not Null)
    - `location` (String)
    - `supplier_id` (ref to foreign key for suppliers)
    - `price_per_unit` (Decimal)
    - `reorder_level` (Integer)
    - `created_at` (Timestamp)
    - `updated_at` (Timestamp)

- **suppliers**
    - `id` (Primary Key, Integer, Auto-increment)
    - `name` (String, Not Null)
    - `contact_email` (String, Not Null)
    - `phone` (String)
    - `address` (String)
    - `location` (String, e.g., city or coordinates)
    - `created_at` (Timestamp)
    - `updated_at` (Timestamp)

- **orders**
    - `id` (Primary Key, Integer, Auto-increment)
    - `item_id` (Foreign Key to inventory.id, Not Null)
    - `employee_id` (Foreign Key to employee.id, Not Null)
    - `supplier_id` (Foreign Key to suppliers.id, Not Null)
    - `quantity` (Integer, Not Null)
    - `status` (Enumeration: 'pending', 'approved', 'rejected', 'delivered')
    - `priority` (Enumeration: 'low', 'normal', 'high')
    - `expected_delivery_date` (Date)
    - `actual_delivery_date` (Date)
    - `created_at` (Timestamp)
    - `updated_at` (Timestamp)
    
- **withdrawals**
    - `id` (Primary Key, Integer, Auto-increment)
    - `requested_by` (Foreign Key to employee.id, Not Null)'
    - `item_id` (Foreign Key to item.id, Not Null)
    - `quantity` (Integer, Not Null)
    - `approved_by` (Foreign Key to employee.id, Nullable)
    - `approval_status` (Enumeration: 'pending', 'approved', 'rejected')
    - `requested_at` (Timestamp)
    - `approved_at` (Timestamp, Nullable)

- **documents**
    - `id` (Primary Key, Integer, Auto-increment)
    - `file_name` (String, Not Null)
    - `file_type` (String, Not Null)
    - `file_size` (Integer, Not Null)
    - `uploaded_by` (Foreign Key to employee.id, Not Null)
    - `order_id` (Foreign Key to orders.id, Nullable)
    - `uploaded_at` (Timestamp)
    
- **supplier_feedback**
    - `id` (Primary Key, Integer, Auto-increment)
    - `supplier_id` (Foreign Key to suppliers.id, Not Null)
    - `order_id` (Foreign Key to orders.id, Not Null)
    - `employee_id` (Foreign Key to employee.id, Not Null)
    - `rating` (Integer, e.g., 1-5)
    - `comments` (Text)
    - `created_at` (Timestamp)

- **notifications**
    - `id` (Primary Key, Integer, Auto-increment)
    - `employee_id` (Foreign Key to employee.id, Not Null)
    - `type` (String, e.g., 'delivery_overrun', 'low_stock', 'supplier_message', 'approval_required')
    - `message` (Text, Not Null)
    - `is_read` (Boolean, Default: false)
    - `created_at` (Timestamp)

---

## 1. Inventory & Order CRUD Operations
**Description:** Create the DB tables above and also create the CRUD (Create, Read, Update, and Delete) operations for all Tables mentioned. 

**Expected Result:** All items in the tables can be added, viewed, edited, and deleted successfully. 

**Notes:** Use a relational database PostgreSQL. Ensure data integrity and validation. 

---

## 2. Inventory & Order List Interface
**Description:** Design a responsive UI to display the data in the database. 

**Expected Result:** All tables are displayed in a user-friendly interface with sorting and filtering capabilities.

---

## 3. User Authentication
**Description:** Implement login and registration forms with validation and user management for supply chain staff.

**Expected Result:** Employees can register and log in securely. Invalid inputs are handled gracefully.

**Notes:** Use hashed passwords and session management. 

---

## 4. Purchase Order Creation Form
**Description:** Build a form to request new purchase orders with validation and error handling.

**Expected Result:** Users can submit new purchase orders with valid inputs. Errors are shown for invalid data.

**Notes:** Include fields like item, quantity, supplier, expected delivery date, and priority.

---

## 5. Withdrawal of stock
**Description:** Build a form to withdraw stock from inventory with validation and error handling. If stock with value > 500€ is withdrawn, a manager needs to approve it.

**Expected Result:** Users can withdraw stock with valid inputs. Errors are shown for invalid data.

**Notes:** Include fields like item, quantity, supplier, expected delivery date. Withdrawals are tracked in the withdrawals table.

---

## 6. Supplier Weather API Integration
**Description:** Integrate a weather API to show weather info for supplier locations (affecting delivery). When a supplier page is opened, display the weather on the same page. 

**Expected Result:** Weather information is displayed based on the supplier's location for each order.

**Notes:** Use an API like OpenWeatherMap. Handle API errors and rate limits.

---

## 7. Notifications
**Description:** Implement in-app and email notifications for delivery overruns, low stock, or supplier messages.

**Expected Result:** Users receive notifications for upcoming delivery deadlines, low inventory, or supplier updates.

**Notes:** Use libraries like SMTP for email. Show real-time updates as banners when stock drops below certain threshold. Store notifications in the notifications table.

---

## 8. Role Management
**Description:** Create role-based access control for different user types (Manager vs. Staff).

**Expected Result:** Managers have access to all features, while staff have limited access (cannot approve large purchases or withdrawals, can´t approve new users etc.).

**Notes** Apply the access control to the existing pages and logic as it seems suitable. 

---

## 9. Data Visualization
**Description:** Display charts showing inventory levels, order frequency, and supplier performance over time on a dedicated page. 

**Expected Result:** Users can view visual analytics of stock trends, order rates, and supplier reliability.

---

## 10. Approval Workflow for Large Purchases
**Description:** When an employee creates a purchase order above a certain threshold (>€3000), it requires manager approval. Create an approval workflow, where the manager can request more information. 

**Expected Result:** Orders above the threshold are flagged for approval and cannot be processed until approved by a manager. If the manager declines the order, it is marked as rejected and the requester can add more information and resubmit.

**Notes:** Notify managers and employees of pending approvals/ information request and allow them to approve, put on hold or reject requests. Use the notifications table for tracking.

---

## 11. Document Upload & Management
**Description:** Allow users to upload, view, and manage documents related orders (e.g., invoices, delivery notes).

**Expected Result:** Users can attach documents to orders/items and retrieve them as needed.

**Notes:** Support PDF and image formats. Enforce file size/type restrictions. Store document metadata in the documents table.

---

## 12. Supplier Performance Feedback Collection
**Description:** After each completed order, prompt users to rate and provide feedback on supplier performance.

**Expected Result:** Feedback is collected and aggregated for each supplier, visible in supplier profiles.

**Notes:** Use rating scales and optional comments. Display average ratings in supplier analytics. Store feedback in the supplier_feedback table.
