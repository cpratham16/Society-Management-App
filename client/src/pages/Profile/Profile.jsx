import Member from './Member/Member';
import ProfileAdvertise from './ProfileAdvertise/ProfileAdvertise';
import ProfileInfo from './ProfileInfo/ProfileInfo';
import { useLoadingWithRefresh} from '../../useLoadingWithRefresh/useLoadingWithRefresh';
import { useGlobalContext } from '../../context/context'
import { Loader,Copyright } from '../../import';
import { useEffect } from 'react';
const Profile = () => {
    const {user}=useGlobalContext();
    const {loading}=useLoadingWithRefresh();
     useEffect(()=>{
       document.title='Profile - Digital Society'
     },[])

    if(loading){
      return <Loader message="Loading, please wait.." />;
    }

    if(!user){
      return <Loader message="Fetching your profile..." />;
    }

    return (
        <>
         <div className="container container-margin-top">
           <ProfileInfo user={user}  />
           <Member members={user.members || []} />
           <ProfileAdvertise user={user}/>     
        </div>
        <Copyright/>   
        </>
    )
}

export default Profile
