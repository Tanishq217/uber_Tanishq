Exercise 1: SRP — Student Onboarding Registration
The Question/Context: You have a system that registers new students. It takes a raw text line (e.g., name=Riya;email=riya@sst.edu...), parses it, checks if the data is valid, creates a student ID, saves it to a database, and prints a receipt.

The Problem: The original OnboardingService was a "God Class". It did everything in one single method. This is bad because if you want to change how you save data, you might accidentally break how you validate data.

The Solution: We applied the Single Responsibility Principle (SRP) by breaking the work into specialized classes:
 
InputParser: Only handles splitting the raw text.

StudentValidator: Only handles the rules (e.g., email must have @).

OnboardingPrinter: Only handles printing to the console.

StudentRepository (Interface): We added this so the service doesn't depend on a specific database, making it easier to switch storage in the future.

Class Explanation: "We moved away from a messy 'do-it-all' method to a modular design where every class has only one reason to change."

Exercise 2: SRP — Campus Cafeteria Billing
The Question/Context: A cafeteria system generates invoices. It looks up prices, calculates tax and discounts, prints the bill, and saves it.

The Problem: The CafeteriaSystem was mixing business logic (tax rates/discounts) with technical logic (saving files/formatting text). If the government changes the tax rate, you shouldn't have to edit the code that handles file storage.

The Solution: We further refined SRP by separating "Rules" from "Actions":

TaxProvider & DiscountProvider (Interfaces): We moved hard-coded percentages into these interfaces.

BillingFormatter: A dedicated class to handle how the bill looks.

InvoiceRepository (Interface): Decoupled the saving logic from the concrete FileStore.

Class Explanation: "We separated the 'What' (the math rules) from the 'How' (printing and saving), ensuring that changes to cafeteria policies don't break the system's infrastructure."

Exercise 3: OCP — Placement Eligibility Rules Engine
The Question/Context: A system determines if a student is eligible for placements based on CGR, attendance, credits, and disciplinary flags.

The Problem: The logic used a giant if-else chain. If the college adds a new rule (like "no backlogs"), you have to modify the existing evaluate method. This violates the Open-Closed Principle (OCP).

The Solution: We made the system "Open for Extension but Closed for Modification":

EligibilityRule (Interface): Every rule (CgrRule, AttendanceRule, etc.) now implements this interface.

Rule List: The engine now just loops through a list of rules. To add a new rule, you just create a new class and add it to the list; you never touch the engine's main loop logic again.

Class Explanation: "Instead of using a hard-coded if-else chain, we used a 'Rule Interface' strategy. This allows us to add new eligibility rules without risking bugs in the existing evaluation code."

Exercise 4: OCP — Hostel Fee Calculator
The Question/Context: A system calculates monthly hostel fees based on room type (Single, Double, etc.) and add-ons (Mess, Gym).

The Problem: It used a switch-case for rooms and if-else for add-ons. Adding a new room type required editing the core calculation method, which is risky for business-critical software.

The Solution: We applied OCP using a Map-based approach:

Pricing Maps: We created static Maps that store the price for each room type and add-on.

Generic Lookup: The calculator now just looks up the price in the Map. It doesn't care which room it is; it just does the math.

Class Explanation: "By moving prices into Maps, we removed the rigid switch-cases. Now, adding a new room type is as simple as adding a single line to a price list, keeping our main calculation logic safe and untouched."

Exercise 5: LSP — File Exporter Hierarchy
The Question/Context: A tool exports data into PDF, CSV, and JSON formats.

The Problem: Subclasses were "misbehaving" and breaking the parent class's promises. PdfExporter would crash if text was too long, and CsvExporter was corrupting data by deleting commas. This violates the Liskov Substitution Principle (LSP).

The Solution: We ensured all subclasses are Substitutable:

Removing Restrictions: We removed the character limit in PDF so it behaves like a normal exporter.

Data Integrity: We fixed the CSV exporter to "escape" commas (wrap them in quotes) so no data is lost during the process.

Class Explanation: "LSP is about trust. We fixed the exporters so that any part of our app can use a generic 'Exporter' without worrying that a specific type (like PDF) will suddenly crash the program."

Exercise 6: LSP — Notification Sender Inheritance
The Question/Context: A system sends notifications via Email, SMS, and WhatsApp.

The Problem: Subclasses had hidden requirements. WhatsAppSender crashed if a number didn't start with +, and EmailSender was silently cutting off long messages.

The Solution: We standardized the "Contract" for sending messages:

No Surprises: We removed the manual crash in WhatsApp and the silent cutting in Email.

SMS Subject Handling: We ensured that SmsSender acknowledges all data sent to it, even if it has to format it differently, so no information is ignored.

Class Explanation: "We made sure that all notification channels honor the same rules. If you tell the system to send a message, every channel will now try its best to send the full message without crashing or silently deleting your data."