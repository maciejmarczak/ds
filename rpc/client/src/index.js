const grpc = require('grpc');

import ProtosLoader from "./ProtosLoader";

function main() {
    let protos = new ProtosLoader('../protos/').loadProtos();

    let client = new protos.LoginService
        ('localhost:50001', grpc.credentials.createInsecure());

    client.login({id: "100"}, function(err, response) {
    });
}

main();