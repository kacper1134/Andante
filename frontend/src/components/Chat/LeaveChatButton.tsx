import { Button } from "@chakra-ui/react";
import { useLeaveChatMutation } from "../../store/api/chat-api-slice";

type LeaveChatButtonProps = {
    username: string;
    activeChatId: number;
}

const LeaveChatButton = ({username, activeChatId}: LeaveChatButtonProps) => {
  const [leaveChat] = useLeaveChatMutation();  

  return (
    <Button
      className="ce-danger-button"
      colorScheme="red"
      variant="outline"
      w="calc(100% - 24px)"
      rounded="2xl"
      position="relative"
      left="12px"
      onClick={() =>
        leaveChat({
          body: { username: username },
          chatId: activeChatId,
        })
      }
    >
      Leave Chat
    </Button>
  );
};

export default LeaveChatButton;
