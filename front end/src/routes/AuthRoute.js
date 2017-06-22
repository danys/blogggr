import React from 'react';
import {
    Route,
    Redirect
} from 'react-router-dom';

export class AuthRoute extends React.Component{

    constructor(props) {
        super(props);
    }


    render(){
        const {render: rMethod, ...rest} = this.props;
        return (
            this.props.loggedin && this.props.loggedin()===true ?
                <Route {...rest} render={rMethod}/>
                    :
                <Route {...rest} render={props => (
                    <Redirect to={{
                        pathname: '/login',
                        state: { from: props.location }
                    }}/>
                )}/>
        );
    }

}
