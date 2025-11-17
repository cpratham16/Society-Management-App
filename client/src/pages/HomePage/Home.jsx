import {HeroSection,ManagmentPeoples,SocietyRules,Gallery,Event,BusinessAdvertiseHomePage,Loader} from '../../import';
import {getHomePageData} from '../../http';
import {useCallback, useEffect, useState} from 'react';

const defaultHomeState = {
  events: [],
  management: [],
  advertise: [],
};

const Home = () => {
  const [data, setData] = useState(defaultHomeState);
  const [loading, setLoading] = useState(false);

  const getData = useCallback(async () => {
    setLoading(true);
    try {
      const { data: homeData } = await getHomePageData();
      if (homeData) {
        setData((prev) => ({
          ...prev,
          ...homeData,
        }));
      }
    } catch (error) {
      console.error('Failed to fetch home page data', error);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    document.title = 'Digital Society';
    getData();
  }, [getData]);

  return (
    <>
      <HeroSection />
      {loading ? (
        <Loader message="loading.." type="true" />
      ) : (
        <>
          <Event events={data.events} />
          <BusinessAdvertiseHomePage advertises={data.advertise} />
          <Gallery />
          <ManagmentPeoples management={data.management} />
        </>
      )}
      <SocietyRules />
    </>
  );
};

export default Home;
