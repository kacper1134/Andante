import { getDownloadURL, ref } from "firebase/storage";
import { useEffect, useState } from "react";
import storage from "../config/firebase-config";

const useFirebaseWithMultipleImages = (urls: string[], fallback: string) => {
  const [images, setImages] = useState<string[]>([]);

  useEffect(() => {
    const fetchImage = async (url: string) => {
      if(url === undefined) return fallback;  
      return await getDownloadURL(ref(storage, url));
    };

    const fetchImages = async (urls: string[]) => {
      return await Promise.all(
        urls.map((url) =>
          fetchImage(url)
            .then(result => result)
            .catch(() => fallback)
        )
      );
    };

    const setImagesUrls = async (urls: string[]) => {
      setImages(await fetchImages(urls));
    };
    
    setImagesUrls(urls);
  }, [fallback, JSON.stringify(urls)]);

  return images;
};

export default useFirebaseWithMultipleImages;
