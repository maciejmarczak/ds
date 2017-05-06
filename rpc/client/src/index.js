const grpc = require('grpc');

const servHost = 'localhost:50001';
const credentials = grpc.credentials.createInsecure();

// load all protos into global context
import ProtosLoader from "./ProtosLoader";
global.protos = new ProtosLoader('../protos/').loadProtos();

global.client = new protos.LoginService(servHost, credentials);
global.patient = new protos.PatientService(servHost, credentials);

import AuthComponent from "./AuthComponent";
import { DoctorHandler, PatientHandler,
    TechnicianHandler } from "./user/UserHandler";

class Application {
    authComponent = new AuthComponent(Application.handleUser);

    start() {
        this.authComponent.login();
    }

    static handleUser(user) {
        let handlers = {
            'DOCTOR': new DoctorHandler(user),
            'PATIENT': new PatientHandler(user),
            'TECHNICIAN': new TechnicianHandler(user)
        };

        handlers[user.role].showMenu();
    }
}

new Application().start();