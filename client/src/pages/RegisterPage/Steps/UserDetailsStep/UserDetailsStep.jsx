import { useEffect } from "react";
import { useHistory } from "react-router-dom";
import { useGlobalContext } from "../../../../context/context";
import Styles from "./UserDetailsStep.module.css";

const Step3 = () => {
    const history = useHistory();
    const {verifyUser}=useGlobalContext();

    useEffect(() => {
        window.scrollTo(0,0);
    },[]);

    return (
        <>
            <div className={`${Styles.step3}`} data-aos='fade-right'>
                <div className={`${Styles.successCard} box-shadow`}>
                    <h3>You're all set!</h3>
                    <p>
                        {verifyUser?.email ? `The account for ${verifyUser.email} is ready to go.` : 'Your account is ready to go.'}
                    </p>
                    <p>
                        You can now log in using the credentials you set during registration.
                    </p>
                    <button className={`btnStructure ${Styles.registerBtn}`} onClick={()=>history.push('/login')}>
                        Go to Login
                    </button>
                </div>
            </div>
        </>
    )
}

export default Step3;
