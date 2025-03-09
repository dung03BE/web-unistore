
import { combineReducers } from "redux";
import cartReducer from "./cart";
import userReducer from "./user";
const allReducers = combineReducers({
    cartReducer,
    userReducer
    // theem nhieu reducer
})
export default allReducers;