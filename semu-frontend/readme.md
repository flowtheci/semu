# SEMU Angular Frontend

This is the official SEMU Angular frontend. Follow the instructions below to set up and run the frontend of the SEMU application.

## Prerequisites

Before you begin, make sure you have the following installed:
- Node.js (which includes npm)

You can check if you have Node and npm installed by running `node -v` and `npm -v` in the terminal.

## Setting Up the Project

To set up the project, follow these steps:

1. Clone the SEMU frontend repository to your local machine using:
   ```
   git clone https://github.com/flowtheci/semu/
   ```

2. Navigate to the project directory:
   ```
   cd semu/semu-frontend
   ```

3. Install the project dependencies using npm:
   ```
   npm install --legacy-peer-deps
   ```

   The `--legacy-peer-deps` flag is used to avoid conflicts with peer dependency management in npm versions 7 or later.

## Running the Frontend

To run the Angular frontend on your local machine, use the Angular CLI command:

```
ng serve --c dev
```

This will compile the application and serve it by default on `http://localhost:4200`. Open your browser and navigate to this URL to view the application.

## Configuration

When serving the application, you can specify a configuration file to use by passing the `--c` flag with the name of the configuration file.
The recommended configuration while development is 'dev', which uses the `environment.dev.ts` options (local backend API url and no route guards). If you want to serve the application with the production configuration, use `ng serve` with no flags. This will use the live backend API url and will enable all authentication guards.


## Building for Production

To build the application, run the following command:

```
ng build
```

This will create a `dist/` folder with the compiled assets optimized for production.

## Further Help

To get more help on the Angular CLI, use `ng help` or check out the [Angular CLI Overview and Command Reference](https://angular.io/cli) page.
