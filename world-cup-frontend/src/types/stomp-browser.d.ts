declare module 'stompjs/lib/stomp.min' {
  const Stomp: {
    over(socket: unknown): any;
  };

  export default Stomp;
}
