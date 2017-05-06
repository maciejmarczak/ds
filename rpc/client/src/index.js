const grpc = require('grpc');

// load all protos into global context
import ProtosLoader from "./ProtosLoader";
global.protos = new ProtosLoader('../protos/').loadProtos();

global.client = new protos.LoginService('localhost:50001', grpc.credentials.createInsecure());
global.patient = new protos.PatientService('localhost:50001', grpc.credentials.createInsecure());

import AuthComponent from "./AuthComponent";
import { DoctorHandler, PatientHandler,
    TechnicianHandler } from "./user/UserHandler";

class Application {
    authComponent = new AuthComponent(this.handleUser);

    start() {
        this.authComponent.login();
    }

    handleUser(user) {
        let handlers = {
            'DOCTOR': new DoctorHandler(user),
            'PATIENT': new PatientHandler(user),
            'TECHNICIAN': new TechnicianHandler(user)
        };

        handlers[user.role].showMenu();
    }
}

new Application().start();