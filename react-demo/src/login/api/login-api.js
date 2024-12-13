import {HOST} from '../../commons/hosts';
import RestApiClient from "../../commons/api/rest-client";


const endpoint = {
    login: '/auth/login',
    register: '/auth/register'
};

function logIn(req, callback){
    console.log(HOST.backend_api + endpoint.login);
    let request = new Request(HOST.backend_api + endpoint.login , {
        method: 'POST',
        headers : {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(req)
    });

    console.log("URL: " + request.url);

    RestApiClient.performRequest(request, callback);
}

function register(req, callback){
    console.log(HOST.backend_api + endpoint.register);
    let request = new Request(HOST.backend_api + endpoint.register , {
        method: 'POST',
        headers : {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(req)
    });

    console.log("URL: " + request.url);

    RestApiClient.performRequest(request, callback);
}

export {
    logIn,
    register
};
