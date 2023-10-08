import { Box } from "@chakra-ui/react"
import { MdClose, MdMenu} from "react-icons/md";

type MenuToggleProps = {
    onToggle: () => void,
    isOpen: boolean
}

const MenuToggle = ({onToggle, isOpen}: MenuToggleProps) => {
    const iconStyles = {cursor: "pointer"};
    return (
        <Box display={{base: "block", lg: "none"}} onClick={onToggle}>
            {isOpen ? <MdClose style={iconStyles} size="2em" /> : <MdMenu style={iconStyles} size="2em" />}
        </Box>
    )
}

export default MenuToggle;