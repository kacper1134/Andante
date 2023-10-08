export interface NewsletterDTO {
    emailAddress: string,
    subscriptionDate: string,
    isConfirmed: boolean,
};

export function isNewsletterDTO(value: any): value is NewsletterDTO {
    return "emailAddress" in value;
}