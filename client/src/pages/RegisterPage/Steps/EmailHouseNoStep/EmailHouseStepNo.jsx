import { useEffect, useState } from "react";
import {register} from "../../../../http/index";
import { useGlobalContext } from "../../../../context/context";
import Styles from "./EmailHouseNoStep.module.css";
import { Loader } from "../../../../import";
const Step1 = ({onNext,onProgress}) => {
    const {setVerifyUserDetails}=useGlobalContext();
    const [loading,setLoading]=useState(false);
    const [step1Data, setStep1Data] = useState({
    name: "",
    email: "",
    societyCode: "",
    houseNo: "",
    password: "",
  });
  const [unMounted,setUnMounted]=useState(false);
  const [message,setMessage]=useState('');
  const handleChange = (e) => {
    const { name, value } = e.target;
    setStep1Data((prevData) => {
      return { ...prevData, [name]: value };
    });
  };
  async function submit(e){
    e.preventDefault();
    const {name,email,societyCode,houseNo,password}=step1Data;
    if(name && email && password && societyCode && !isNaN(houseNo)){
        setMessage('');
        setLoading(true);
        try{
            const payload = {
                data:{
                    name,
                    email,
                    password,
                    society_code:societyCode,
                    houseNo,
                    phone:"",
                    profession:"Resident",
                }
            };
            const {data} = await register(payload);
            if(!data?.success){
                setMessage(data?.message || 'Unable to start registration. Please try again.');
            }
            else if(!unMounted){
                onProgress();
                setVerifyUserDetails({email});
                onNext();
            }
        }catch(err){
            const errorMsg = err?.response?.data?.message || 'Unable to start registration. Please try again.';
            setMessage(errorMsg);
        }finally{
            if(!unMounted){
                setLoading(false);
            }
        }
    }
    else{
        setMessage('please fill the valid Details.');
    }
  }
  useEffect(()=>{
      return () =>{
         setUnMounted(true);
      }
  },[])
    return loading ? <Loader message='Registering..' type='true'/>:(
        <>
            <div className={Styles.step1} data-aos='zoom-in'>
                <form  className='box-shadow' onSubmit={submit}>
                    <div className={`${Styles.formWrapper}`}>
                        <p>{message}</p>
                    <label htmlFor="name">Full Name</label>
                    <div className={`${Styles.step1Input} ${Styles.idiv}`}>
                        <i className="fas fa-user"></i>
                        <input type="text" name="name"
                        value={step1Data.name} onChange={handleChange}
                        required
                        placeholder='Tony Stark'/>
                    </div>
                    <label htmlFor="email">Email</label>
                    <div className={`${Styles.step1Input} ${Styles.idiv}`} autoFocus>
                       <i className="fas fa-envelope"></i>
                        <input type="email" name="email" 
                        value={step1Data.email} onChange={handleChange}
                        required autoFocus
                        placeholder='example@gmail.com'/>
                    </div>
                    <label>Society Security Code</label>
                    <div className={`${Styles.step1Input} ${Styles.idiv}`}>
                        <i className="fas fa-user"></i>
                        <input type="text" name="societyCode" 
                        value={step1Data.societyCode} onChange={handleChange}
                        required
                        placeholder='ya83gye9'/>
                    </div>
                    <label>House No</label>
                    <div className={`${Styles.step1Input} ${Styles.idiv}`}>
                        <i className="fas fa-home"></i>
                        <input type="text" name="houseNo" value={step1Data.houseNo}
                        required placeholder='ex: 54' onChange={handleChange}/>
                    </div>
                    <label>Create Password</label>
                    <div className={`${Styles.step1Input} ${Styles.idiv}`}>
                        <i className="fas fa-lock"></i>
                        <input type="password" name="password"
                        value={step1Data.password} onChange={handleChange}
                        required placeholder='Min 6 characters'/>
                    </div>
                    <button type='submit' className={`btnStructure ${Styles.nextBtn}`}>
                       Next
                        <i className="fas fa-arrow-right"></i>
                    </button>
                    </div>
                </form>
            </div>
        </>
    )
}

export default Step1;
