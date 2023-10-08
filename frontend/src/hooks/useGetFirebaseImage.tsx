import { getDownloadURL, ref } from "firebase/storage";
import storage from "../config/firebase-config";

const useGetFirebaseImage = () => {
  return async (imagePath: string) => {
    const imageRef = ref(storage, imagePath === "" ? "file.png" : imagePath);
    if(imagePath !== "") return getDownloadURL(imageRef);
    return Promise.resolve(() => "");
  };
};

export default useGetFirebaseImage;
