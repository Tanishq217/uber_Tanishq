# SOLID Principles Practice Exercises

---

# Exercise 1: SRP — Student Onboarding Registration

## The Question/Context

You have a system that registers new students. It takes a raw text line (e.g., `name=Riya;email=riya@sst.edu...`), parses it, checks if the data is valid, creates a student ID, saves it to a database, and prints a receipt.

## The Problem

The original **OnboardingService** was a "God Class". It did everything in one single method. This is bad because if you want to change how you save data, you might accidentally break how you validate data.

## The Solution

We applied the **Single Responsibility Principle (SRP)** by breaking the work into specialized classes:

* **InputParser**: Only handles splitting the raw text.
* **StudentValidator**: Only handles the rules (e.g., email must have `@`).
* **OnboardingPrinter**: Only handles printing to the console.
* **StudentRepository (Interface)**: We added this so the service doesn't depend on a specific database, making it easier to switch storage in the future.

## Class Explanation

> We moved away from a messy "do-it-all" method to a modular design where every class has only one reason to change.

---

# Exercise 2: SRP — Campus Cafeteria Billing

## The Question/Context

A cafeteria system generates invoices. It looks up prices, calculates tax and discounts, prints the bill, and saves it.

## The Problem

The **CafeteriaSystem** was mixing business logic (tax rates/discounts) with technical logic (saving files/formatting text). If the government changes the tax rate, you shouldn't have to edit the code that handles file storage.

## The Solution

We further refined **SRP** by separating **Rules** from **Actions**:

* **TaxProvider & DiscountProvider (Interfaces)**: We moved hard-coded percentages into these interfaces.
* **BillingFormatter**: A dedicated class to handle how the bill looks.
* **InvoiceRepository (Interface)**: Decoupled the saving logic from the concrete `FileStore`.

## Class Explanation

> We separated the **"What" (the math rules)** from the **"How" (printing and saving)**, ensuring that changes to cafeteria policies don't break the system's infrastructure.

---

# Exercise 3: OCP — Placement Eligibility Rules Engine

## The Question/Context

A system determines if a student is eligible for placements based on **CGR, attendance, credits, and disciplinary flags**.

## The Problem

The logic used a giant `if-else` chain. If the college adds a new rule (like **"no backlogs"**), you have to modify the existing `evaluate` method. This violates the **Open-Closed Principle (OCP)**.

## The Solution

We made the system **Open for Extension but Closed for Modification**:

* **EligibilityRule (Interface)**: Every rule (`CgrRule`, `AttendanceRule`, etc.) now implements this interface.
* **Rule List**: The engine now just loops through a list of rules. To add a new rule, you just create a new class and add it to the list; you never touch the engine's main loop logic again.

## Class Explanation

> Instead of using a hard-coded `if-else` chain, we used a **Rule Interface strategy**. This allows us to add new eligibility rules without risking bugs in the existing evaluation code.

---

# Exercise 4: OCP — Hostel Fee Calculator

## The Question/Context

A system calculates monthly hostel fees based on **room type (Single, Double, etc.)** and **add-ons (Mess, Gym)**.

## The Problem

It used a `switch-case` for rooms and `if-else` for add-ons. Adding a new room type required editing the core calculation method, which is risky for business-critical software.

## The Solution

We applied **OCP** using a **Map-based approach**:

* **Pricing Maps**: We created static `Maps` that store the price for each room type and add-on.
* **Generic Lookup**: The calculator now just looks up the price in the `Map`. It doesn't care which room it is; it just does the math.

## Class Explanation

> By moving prices into **Maps**, we removed the rigid switch-cases. Now, adding a new room type is as simple as adding a single line to a price list, keeping our main calculation logic safe and untouched.

---

# Exercise 5: LSP — File Exporter Hierarchy

## The Question/Context

A tool exports data into **PDF, CSV, and JSON formats**.

## The Problem

Subclasses were **misbehaving** and breaking the parent class's promises.

* `PdfExporter` would crash if text was too long.
* `CsvExporter` was corrupting data by deleting commas.

This violates the **Liskov Substitution Principle (LSP)**.

## The Solution

We ensured all subclasses are **Substitutable**:

* **Removing Restrictions**: We removed the character limit in PDF so it behaves like a normal exporter.
* **Data Integrity**: We fixed the CSV exporter to **escape commas** (wrap them in quotes) so no data is lost during the process.

## Class Explanation

> **LSP is about trust.** We fixed the exporters so that any part of our app can use a generic `Exporter` without worrying that a specific type (like PDF) will suddenly crash the program.

---

# Exercise 6: LSP — Notification Sender Inheritance

## The Question/Context

A system sends notifications via **Email, SMS, and WhatsApp**.

## The Problem

Subclasses had hidden requirements.

* `WhatsAppSender` crashed if a number didn't start with `+`.
* `EmailSender` was silently cutting off long messages.

## The Solution

We standardized the **Contract** for sending messages:

* **No Surprises**: We removed the manual crash in WhatsApp and the silent cutting in Email.
* **SMS Subject Handling**: We ensured that `SmsSender` acknowledges all data sent to it, even if it has to format it differently, so no information is ignored.

## Class Explanation

> We made sure that all notification channels honor the same rules. If you tell the system to send a message, every channel will now try its best to send the full message without crashing or silently deleting your data.

---

# Exercise 1: Student Onboarding Registration (SRP)

## The Goal

Break down a "God Class" that handles parsing, validation, ID creation, saving, and printing into **single-responsibility units**.

### InputParser.java (New File)

**Workflow:**
Takes the raw string (e.g., `name=Riya;email=riya@sst.edu`). It splits the string by `;` and then by `=` to create a structured `Map<String, String>`.

**Reasoning:**
This isolates the **Extraction logic**. If the input format changes to JSON or CSV, only this file is edited.

---

### StudentValidator.java (New File)

**Workflow:**
Receives the `Map` from the parser. It checks:

* If the name is blank
* If the email contains `@`
* If the program is one of the allowed values (`CSE`, `AI`, `SWE`)

**Reasoning:**
This centralizes **Business Rules**. You can now test rules without needing a database or a console.

---

### StudentRepository.java (New File - Interface)

**Workflow:**
Defines the contract for any storage system:

* `save()`
* `count()`
* `all()`

**Reasoning:**
This decouples the service from the database (**Dependency Inversion**), allowing you to swap `FakeDb` for a real SQL database easily.

---

### FakeDb.java (Modified)

**Workflow:**
We updated this class to implement the `StudentRepository` interface.

**Reasoning:**
It becomes a **pluggable storage component** that fulfills the contract required by the system.

---

### OnboardingPrinter.java (New File)

**Workflow:**
Handles all `System.out.println` calls. It takes success data or error lists and formats them for the user.

**Reasoning:**
This separates **User Interface** from **Logic**. Changing the output language or style only affects this file.

---

### OnboardingService.java (Modified)

**Workflow:**
It is now an **Orchestrator**.

It asks:

* the `InputParser` for data
* the `StudentValidator` for errors
* the `StudentRepository` to save
* the `OnboardingPrinter` to show results

---

# Exercise 2: Campus Cafeteria Billing (SRP)

## The Goal

Separate billing calculations from **tax/discount rules and formatting**.

### TaxProvider.java & DiscountProvider.java (New Interfaces)

**Workflow:**
Define methods to:

* get tax rates
* calculate discounts based on customer types (student/staff)

---

### CafeteriaTaxRules.java & CafeteriaDiscountRules.java (New Files)

**Workflow:**
Contain the specific hard-coded logic:

* `5%` tax for students
* `10.0` discount for subtotals `>= 180.0`

**Reasoning:**
Moves **Policy** out of the **System**. Changing a discount doesn't require touching the billing math logic.

---

### BillingFormatter.java (New File)

**Workflow:**
Uses a `StringBuilder` to assemble the final invoice string with exact formatting (`%.2f`).

**Reasoning:**
Ensures the system doesn't care about string manipulation.

---

### CafeteriaSystem.java (Modified)

**Workflow:**
It now manages the menu and coordinates between the:

* `TaxProvider`
* `DiscountProvider`
* `BillingFormatter`

to generate and save an invoice.

---

# Exercise 3: Placement Eligibility Engine (OCP)

## The Goal

Eliminate a giant `if-else` chain so new rules can be added **without modifying existing code**.

### EligibilityRule.java (New Interface)

**Workflow:**
Every rule must implement:

* `isEligible()` → returns `true/false`
* `getReason()` → the error message

---

### CgrRule.java, AttendanceRule.java, etc. (New Files)

**Workflow:**
Each file handles exactly **one check**.

Example:

* `CgrRule` → checks if `CGR >= 8.0`

---

### EligibilityEngine.java (Modified)

**Workflow:**
It now holds a `List<EligibilityRule>`.

It loops through the list and calls:

```
rule.isEligible()
```

If any rule fails, it breaks the loop and reports the reason.

**Reasoning:**
**OCP Met.** To add a **Backlog Rule**, you just create a new class and add it to the list. The engine's loop code never changes.

---

# Exercise 4: Hostel Fee Calculator (OCP)

## The Goal

Remove `switch-cases` for pricing so new room types can be added via configuration.

### HostelFeeCalculator.java (Modified)

**Workflow:**
We replaced the `switch(roomType)` and `if-else` addon chains with **Static Maps**:

* `ROOM_PRICES`
* `ADDON_PRICES`

**Reasoning:**
The `calculateMonthly` method now performs a generic `Map.get()`.

Adding a new **Deluxe Suite** just requires adding one entry to the map, not editing the math logic.

---

# Exercise 5: File Exporter Hierarchy (LSP)

## The Goal

Ensure subclasses don't break the parent's **Contract**.

### PdfExporter.java (Modified)

**Workflow:**
We removed the code that threw an exception if the content was `> 20` characters.

**Reasoning:**
Subclasses shouldn't be **pickier than the parent**. If `Exporter` says it can export strings, the PDF version shouldn't arbitrarily crash on long ones.

---

### CsvExporter.java (Modified)

**Workflow:**
Instead of deleting commas and newlines (which corrupts data), we wrapped the content in quotes.

**Reasoning:**
A subclass must preserve the **meaning of the data**. Deleting characters changed the data's meaning, violating **LSP**.

---

# Exercise 6: Notification Sender Inheritance (LSP)

## The Goal

Standardize channel behavior so any sender can be used without surprises.

### WhatsAppSender.java (Modified)

Removed the manual crash (`IllegalArgumentException`) for numbers without a `+`.

---

### EmailSender.java (Modified)

Removed the logic that cut off the message at `40` characters.

---

### SmsSender.java (Modified)

Updated to ensure the **subject is no longer ignored**, even if formatted into the body.

---

## Reasoning

This makes the senders **Substitutable**.

The main program can now trust that calling `send()` will actually send the **full data without crashing or losing information**.

















# Adapter Pattern — Payment Gateway Integration

---

# 1. The Problem: "The Incompatible Language"

Imagine you have two friends, one who only speaks **Spanish** and one who only speaks **Japanese**. If you want to ask them both to **"Buy Lunch,"** you can't use the same words. You have to learn Spanish for one and Japanese for the other.

In our code, the **OrderService (your application)** had the same problem with two payment providers:

* **FastPayClient**: To pay, you had to call
  `makePayment(String email, double amount)`

* **SafeCashClient**: To pay, you had to call
  `chargeStudent(String studentId, double value)`

## Why this was a mess

### Different Names

* One used **makePayment**
* The other used **chargeStudent**

### Different Data

* One needed an **Email**
* The other needed a **Student ID**

### Hard-coded Logic

The **OrderService** had to contain a giant `if-else` block to check which bank was being used and then manually change the data to fit that bank.

If you added a **3rd bank**, you would have to break the code again.

---

# 2. The Solution: The "Universal Translator" (Adapter Pattern)

We solved this by creating a **Target Interface**.

Think of this as a **Standard Rule** that says:

> In this app, we only use one command:
> `pay(studentId, amount)`

We then built **Adapters (Translators)** for each bank.

These adapters take our **standard command** and **translate it into the specific language that the bank understands**.

---

# 3. How it Works (File-by-File Workflow)

---

## Step 1: Create the Standard (PaymentGateway.java)

This is a simple **Interface**.

It acts as the **Law** for our system.

It says any payment method must have a method called:

```
pay(String studentId, double amount)
```

### Job

Unify the command so the **OrderService** doesn't have to worry about different names.

---

## Step 2: Build the FastPay Translator (FastPayAdapter.java)

This class **implements the PaymentGateway interface**.

### What it does

When the app calls:

```
pay(studentId, amount)
```

this adapter internally:

1. Looks up the student's **email**
2. Calls the bank's actual method

```
makePayment(email, amount)
```

### Job

It hides the fact that **FastPay is different** (because it uses email).

---

## Step 3: Build the SafeCash Translator (SafeCashAdapter.java)

This class also **implements the PaymentGateway interface**.

### What it does

It takes the standard:

```
pay(studentId, amount)
```

and simply forwards it to the bank's method:

```
chargeStudent(studentId, value)
```

### Job

It **renames the bank's confusing method** to our standard one.

---

## Step 4: Clean up the Manager (OrderService.java)

We removed all the **if-else logic** from here.

### What it does now

It simply takes a **PaymentGateway object** in its constructor.

When it's time to check out, it just calls:

```
gateway.pay(id, amount)
```

### Job

It no longer cares which bank is being used.

It just trusts the **Translator (Adapter)** to handle the details.

---

# 4. Summary for your Class Explanation

When your instructor asks what you did, say this:

### Standardization

> I created a common interface called **PaymentGateway** so that my main application code only has to learn **one way to process payments**.

---

### Encapsulation

> I moved all the messy, bank-specific details (like converting IDs to Emails) into **separate Adapter classes**.
> This follows the **Single Responsibility Principle**.

---

### Decoupling

> By using the **Adapter Pattern**, I made my **OrderService flexible**.

If we want to add a new bank like **Paytm** tomorrow:

* We just write **one new Adapter**
* We **never touch OrderService**

This satisfies the **Open-Closed Principle**.

---

# The Result

The code is now:

* Clean
* Easy to test
* Ready for **any number of new payment methods**
