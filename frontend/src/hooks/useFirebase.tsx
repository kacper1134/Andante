import { getDownloadURL, ref } from "firebase/storage";
import { useEffect, useState } from "react";
import storage from "../config/firebase-config";

export function useFirebase(url: string, fallback: string): string {
    const [image, setImage] = useState<string>(fallback);

    useEffect(() => {
        const fetchData = async () => {
            const downloadUrl = await getDownloadURL(ref(storage, url));

            setImage(downloadUrl);
        }

        fetchData().catch(() => setImage(fallback));
    }, [url, fallback]);

    return image;
} 