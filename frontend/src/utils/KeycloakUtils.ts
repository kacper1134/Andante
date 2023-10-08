import { KeycloakTokenParsed } from "keycloak-js";
import { DateTime } from "luxon";

export interface UserDetails {
    delivery: {
        country: string,
        city: string,
        street: string,
        postalCode: string,
    },
    personal: {
        accountCreated: DateTime,
        dateOfBirth: DateTime,
        emailAddress: string,
        name: string,
        surname: string,
        username: string,
        gender: string,
        phoneNumber: string,
        description: string,
    },
};

export function getUserDetails(idToken: KeycloakTokenParsed): UserDetails {
    return {
        delivery: {
            country: idToken.delivery.country,
            city: idToken.delivery.city,
            street: idToken.delivery.street === "" ? "" : idToken.delivery.street,
            postalCode: idToken.delivery.postal_code === "" ? "" : idToken.delivery.postal_code,
        },
        personal: {
            accountCreated: DateTime.fromMillis(idToken.personal.created_timestamp),
            dateOfBirth: DateTime.fromISO(idToken.personal.date_of_birth),
            emailAddress: idToken.personal.email,
            name: idToken.personal.first_name,
            surname: idToken.personal.last_name,
            username: idToken.personal.username,
            gender: idToken.personal.gender,
            phoneNumber: idToken.personal.phone_number,
            description: idToken.personal.description ? idToken.personal.description : "Not provided",
        }
    }
};