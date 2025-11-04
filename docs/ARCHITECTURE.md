# Micuota MVP Architecture & Integrations

## Introduction
Organizing trail running training registrations seemed simple at first, but it quickly became clear there was a problem: most of the process was manual. Checking payments one by one, updating spreadsheets, and replying to participants took way too much time. On top of that, there wasn’t a single place to centralize all the information — which led to duplicated work and a higher chance of errors.

That pain point sparked the idea of creating my own application to manage the entire flow. The main goal was to automate repetitive tasks and bring more transparency for both organizers and participants.

The solution I developed is split into two main parts:

- **Public area**: where athletes can register and track their payment status.
- **Private area (dashboard)**: designed for organizers, with reports and real-time tracking of registrations.

In addition, the application exposes a set of APIs that connect directly to the database and the Mercado Pago webhook, ensuring that each payment is automatically confirmed without the need for manual checks.

This first version already brought a huge efficiency gain and paved the way for further system improvements in the future.

## Architecture
The idea from the start was to create a fullstack MVP using Next.js, leveraging the framework’s flexibility to build both the frontend and backend within the same project. The goal was to get something functional without wasting time on complex setup or multiple repositories.

The architecture was organized in a simple way, splitting responsibilities into:

### Public area (`/public`)
Pages accessible to participants, such as registration, login, and payment status.

### Private area (`/private/dashboard`)
Built for organizers, with a dashboard to track registrations, reports, and administrative info.

### Advantages of this separation
- **Clear architecture** – It’s immediately clear which routes are public and which are restricted. Makes onboarding and maintenance easier: any developer opening the folder can instantly understand the structure.
- **Independent layouts** – The public area can have a simple, conversion-focused layout (e.g., header with login/register). The private area can have a more robust layout, with sidebar, topbar, breadcrumbs, charts, etc. Each `layout.tsx` only manages what’s necessary for its own context.
- **Decoupled authentication and authorization** – In the private area’s `layout.tsx`, you can validate session/token before rendering. The public area doesn’t need to load unnecessary security logic.
- **Performance and optimized loading** – Components, providers, and middleware loaded in each area are specific to that area. This prevents public pages from inheriting heavy dependencies (like dashboard, charts, etc.).
- **Controlled reusability** – Each area can have its own providers (e.g., theme, user context, reporting data). The public area can use different providers (e.g., analytics, checkout flow).
- **Scalability** – If the project grows in the future (e.g., organizers area, athletes area, admin area), you just create new folders with specific `layout.tsx`. This gives the application natural modularity.

### APIs (`/api`)
Next.js routes acting as the backend, handling database integrations and external services. Some examples:

- `checkout-mercado-pago`: starts the payment flow.
- `customer`: manages participant data.
- `flags`: controls app features and behaviors.

### Support layer
Folders such as `components`, `hooks`, `utils`, `requests`, and `shared` provide organization and code reusability.

This separation brought clarity from the very beginning and helped keep the project simple yet scalable. The structure makes it easy to grow (adding new pages, APIs, or integrations) while remaining maintainable.

## Mercado Pago Integration
One of the most challenging parts of this MVP was integrating with Mercado Pago. The official documentation is somewhat outdated in places, which meant I had to spend more time than expected getting everything production-ready.

The product I chose was Checkout Pro, which redirects the user to Mercado Pago’s site to complete the payment. While it was the fastest option to implement, it came with some production challenges (which I’ll detail later).

In general, I had to implement three main parts:

1. Creating the payment preference
2. Validating the return after payment
3. Webhook for automatic payment notifications

### 1. Creating the payment preference
Everything starts when the user completes their registration. At that moment, I generate a payment preference that contains the buyer’s info and the order reference. This preference returns the well-known `init_point`, which is the URL to redirect the user for payment.

In short: when the registration is completed, I generate the preference and send back the `initPoint` to redirect the athlete to Mercado Pago.

### 2. Validating the return after payment
After payment, Mercado Pago redirects to the configured `back_urls`. At this route, I confirm the payment status to prevent fraud.

Here, I only accept the payment as valid if the status is `approved`. Otherwise, I redirect the user to a failure page.

### 3. Payment confirmation webhook
The final step (and arguably the most important) was setting up the webhook. It ensures that even if the user closes their browser before returning to the app, the payment is still processed correctly.

With this in place, even if the user flow fails, the server will always have the confirmed payment info.

> ⚠️ **Important:** besides exposing this route in your app, you must also configure it in the Mercado Pago dashboard under the Webhooks section. Otherwise, Mercado Pago won’t know where to send automatic payment notifications.

This combination (preference → callback → webhook) completed the Mercado Pago integration. Despite the outdated docs and some manual adjustments, Checkout Pro worked well for the MVP.

## Supabase Integration
To store participant data, I used Supabase as the database. Since the initial need was simple — just keeping user registrations — I created a table called `database-example`, without relations to other tables.

This table stores basic info such as name, email, document, age, gender, and whether the user has already paid.

The integration was straightforward and centered around three main operations: create, edit, and query customers.

### 1. Creating a new customer
- Create a record with the participant’s information when they complete the form.

### 2. Querying a customer by document
- Retrieve the current state of a participant by their document number, allowing organizers to quickly check payment status.

### 3. Editing customer info
- Update fields like payment status or contact information when new data arrives from Mercado Pago or manual adjustments are needed.

### 4. Filtering customers
Beyond basic CRUD, I also implemented filter routes to extract simple reports about participants. The goal was to answer questions like:

- How many people have paid?
- How many are women?
- What’s the average participant age?

Filters were split into routes such as `/customer/filter/by-age`, `/customer/filter/by-gender`, and `/customer/filter/by-payment`.

These filters were essential for generating quick reports about participants and making event organization easier without needing external tools.

## Production Issues
So far, the Mercado Pago integration had worked well in tests. But as usual, production is another world. A few unexpected problems came up, mainly related to browser behavior and the redirect flow.

### 1. Instagram Webview
Since most of the event’s promotion was done on Instagram, many users accessed the registration link via bio or stories. The catch is that Instagram opens links inside an internal webview (you remain inside the app instead of opening a real browser).

That’s when issues appeared:

- When trying to redirect to Mercado Pago, Instagram flagged the action as potentially malicious.
- As a result, the redirect simply didn’t happen — the flow froze inside the app.

**Solution:** I improved the UX/UI to guide users to open the link outside Instagram. I built a page explaining the problem and added a clear “Open in browser” button. This small change significantly boosted conversion rates, as the flow was no longer blocked by Instagram.

### 2. Safari and automatic redirects
Another challenge came from Safari, which doesn’t handle automatic redirects very well. In some cases, users simply weren’t sent to Mercado Pago.

**Solution:** Again, UX came to the rescue. Instead of relying only on automatic redirects, I also added a manual action: a visible “Click here to continue payment” button, ensuring the flow worked even when Safari blocked redirects.
