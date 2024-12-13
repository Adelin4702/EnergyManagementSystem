import {HOST} from '../../commons/hosts';
import RestApiClient from "../../commons/api/rest-client";


const endpoint = {
    person: '/person'
};

function getPersons(callback) {
    const jwt = sessionStorage.getItem('jwt');
    let request = new Request(HOST.backend_api + endpoint.person, {
        method: 'GET',
        headers:{
        'Authorization': `Bearer ${jwt}`
        }
    });
    console.log(request.url);
    RestApiClient.performRequest(request, callback);
}

function getPersonById(params, callback){
    let request = new Request(HOST.backend_api + endpoint.person + params.id, {
       method: 'GET'
    });

    console.log(request.url);
    RestApiClient.performRequest(request, callback);
}

function postPerson(user, callback){
    const jwt = sessionStorage.getItem('jwt');
    let request = new Request(HOST.backend_api + endpoint.person , {
        method: 'POST',
        headers : {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${jwt}`
        },
        body: JSON.stringify(user)
    });

    console.log("URL: " + request.url);

    RestApiClient.performRequest(request, callback);
}

function updatePerson(user, callback){
    const jwt = sessionStorage.getItem('jwt');
    let request = new Request(HOST.backend_api + endpoint.person + '/' + user.id , {
        method: 'PUT',
        headers : {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${jwt}`
        },
        body: JSON.stringify(user)
    });

    console.log("URL: " + request.url);

    RestApiClient.performRequest(request, callback);
}

function deletePerson(params, callback){
    const jwt = sessionStorage.getItem('jwt');
    let request = new Request(HOST.backend_api + endpoint.person + '/' + params.id, {
       method: 'DELETE',
       headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${jwt}`
        }
    });

    console.log(request.url);
    RestApiClient.performRequest(request, callback);
}

export {
    getPersons,
    getPersonById,
    postPerson,
    deletePerson,
    updatePerson
};
