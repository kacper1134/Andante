import sanityClient from '@sanity/client'
import imageUrlBuilder from '@sanity/image-url'

export const readClient = sanityClient({
    projectId: 'vazddj0l',
    dataset: 'production',
    apiVersion: '2022-10-22',
    useCdn: false,
    token: process.env.REACT_APP_PUBLIC_SANITY_TOKEN
});

export const writeClient = sanityClient({
    projectId: 'vazddj0l',
    dataset: 'production',
    apiVersion: '2022-10-22',
    useCdn: false,
    token: process.env.REACT_APP_PUBLIC_SANITY_TOKEN
});

const builder = imageUrlBuilder(readClient);

export const urlFor = (source) => builder.image(source);