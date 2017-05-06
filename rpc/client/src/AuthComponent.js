var readlineSync = require('readline-sync');

class AuthComponent {
    loginTries = 0;
    maxTries = 3;

    success;

    constructor(success) {
        this.success = success;
    }

    login() {
        this.loginTries += 1;

        let id = readlineSync.question('Login using id: ');

        client.login({ id: id }, (err, response) => {
            if (!response.id) {
                if (this.maxTries >= this.loginTries + 1) {
                    this.login();
                } else {
                    console.log(this.maxTries + ' tries utilized. Exiting.');
                    process.exit();
                }
            } else {
                this.success(response);
            }
        });
    }
}

export default AuthComponent;